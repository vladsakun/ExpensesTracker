package com.emendo.expensestracker.transactions.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.domain.currency.ConvertCurrencyUseCase
import com.emendo.expensestracker.core.domain.transaction.GetTransactionsSumUseCase
import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import com.emendo.expensestracker.core.model.data.TransactionType.Companion.id
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManagerDelegate
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action.Companion.DangerAction
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.create.transaction.api.CreateTransactionScreenApi
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.extensions.abs
import com.emendo.expensestracker.data.api.manager.ExpeTimeZoneManager
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionValueWithType
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import com.emendo.expensestracker.data.api.repository.UserDataRepository
import com.emendo.expensestracker.model.ui.NetworkViewState
import com.emendo.expensestracker.model.ui.resourceValueOf
import com.emendo.expensestracker.transactions.TransactionsListArgs
import com.emendo.expensestracker.transactions.destinations.TransactionsListRouteDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import java.time.ZoneId
import javax.inject.Inject

private const val TRANSACTIONS_LIST_DELETE_TRANSACTION_DIALOG = "transactions_list_delete_transaction_dialog"

@HiltViewModel
class TransactionsListViewModel @Inject constructor(
  private val transactionRepository: TransactionRepository,
  timeZoneManager: ExpeTimeZoneManager,
  private val amountFormatter: AmountFormatter,
  private val getTransactionsSumUseCase: GetTransactionsSumUseCase,
  private val convertCurrencyUseCase: ConvertCurrencyUseCase,
  private val createTransactionScreenApi: CreateTransactionScreenApi,
  private val userDataRepository: UserDataRepository,
  savedStateHandle: SavedStateHandle,
) : ViewModel(), TransactionListCommander, ModalBottomSheetStateManager by ModalBottomSheetStateManagerDelegate() {

  private val args: TransactionsListArgs? by lazy(LazyThreadSafetyMode.NONE) {
    TransactionsListRouteDestination.argsFrom(savedStateHandle).args
  }

  private val transactions: Flow<PagingData<UiModel.TransactionItem>> = getTransactionsFlow()
    .map { it.map(UiModel::TransactionItem) }

  private val list: Flow<PagingData<UiModel>> =
    combine(
      timeZoneManager.timeZoneState,
      transactions,
      userDataRepository.generalCurrency,
    ) { zoneId: ZoneId, pagingData: PagingData<UiModel.TransactionItem>, generalCurrency ->
      transformPagingData(pagingData, zoneId, generalCurrency)
    }.cachedIn(viewModelScope)

  internal val state: StateFlow<NetworkViewState<TransactionsUiState>> =
    transactionUiState(transactionRepository, list)
      .stateInWhileSubscribed(scope = viewModelScope, initialValue = NetworkViewState.Idle)

  override fun showConfirmDeleteTransactionBottomSheet(transaction: TransactionModel) {
    showModalBottomSheet(
      GeneralBottomSheetData
        .Builder(
          id = TRANSACTIONS_LIST_DELETE_TRANSACTION_DIALOG,
          positiveAction = DangerAction(resourceValueOf(R.string.delete)) { deleteTransaction(transaction.id) },
        )
        .title(resourceValueOf(R.string.transactions_list_dialog_delete_transaction_title))
        .negativeAction(Action(resourceValueOf(R.string.cancel), ::hideModalBottomSheet))
        .build()
    )
  }

  private fun deleteTransaction(transactionId: Long) {
    viewModelScope.launch {
      transactionRepository.deleteTransaction(transactionId)
      hideModalBottomSheet()
    }
  }

  private fun transformPagingData(
    pagingData: PagingData<UiModel.TransactionItem>,
    zoneId: ZoneId,
    generalCurrency: CurrencyModel,
  ): PagingData<UiModel> {
    return pagingData.insertSeparators { before: UiModel.TransactionItem?, after: UiModel.TransactionItem? ->
      if (after == null) {
        // we're at the end of the list
        return@insertSeparators null
      }

      if (before == null) {
        // we're at the beginning of the list
        return@insertSeparators SeparatorItem(zoneId, after, generalCurrency)
      }

      // check between 2 items
      if (isSameGroup(before, after, zoneId)) {
        // no separator
        null
      } else {
        SeparatorItem(zoneId, after, generalCurrency)
      }
    }
  }

  private suspend fun SeparatorItem(
    zoneId: ZoneId,
    after: UiModel.TransactionItem,
    generalCurrency: CurrencyModel,
  ): UiModel.SeparatorItem {
    val timeZone = TimeZone.of(zoneId.id)
    val from = after.transaction.date.toLocalDateTime(timeZone).date.atStartOfDayIn(timeZone)
    val to = from.plus(DateTimePeriod(days = 1), timeZone)

    val transactions = retrieveTransactionInPeriod(from, to)
    val transactionWithConvertedValues = transactions.map { transaction ->
      if (transaction.currency == generalCurrency) {
        return@map transaction
      }

      val convertedValue = convertCurrencyUseCase.invoke(
        value = transaction.value,
        fromCurrencyCode = transaction.currency.currencyCode,
        toCurrencyCode = generalCurrency.currencyCode,
        usdToOriginalRate = transaction.usdToOriginalRate,
        conversionDate = transaction.date,
      )
      transaction.copy(value = convertedValue)
    }
    val total = getTransactionsSumUseCase(transactionWithConvertedValues)
    return UiModel.SeparatorItem(
      instant = after.transaction.date,
      sum = amountFormatter.format(total, generalCurrency).formattedValue,
    )
  }

  private suspend fun retrieveTransactionInPeriod(from: Instant, to: Instant): List<TransactionValueWithType> =
    when (val arguments = args) {
      null -> transactionRepository.retrieveTransactionsInPeriod(from, to)

      is TransactionsListArgs.TransactionListArgsByType ->
        transactionRepository.retrieveTransactionsByTypeInPeriod(
          transactionType = arguments.transactionType,
          from = from,
          to = to,
        )

      is TransactionsListArgs.TransactionListArgsByCategory ->
        transactionRepository.retrieveTransactionsByCategoryInPeriod(
          categoryId = arguments.categoryId,
          from = from,
          to = to,
        )

      is TransactionsListArgs.TransactionListArgsBySubcategory ->
        transactionRepository.retrieveTransactionsBySubcategoryInPeriod(
          subcategoryId = arguments.subcategoryId,
          from = from,
          to = to,
        )
    }

  sealed class UiModel {
    data class TransactionItem(val transaction: TransactionModel) : UiModel()
    data class SeparatorItem(val instant: Instant, val sum: String?) : UiModel()
  }

  private fun isSameGroup(before: UiModel.TransactionItem, after: UiModel.TransactionItem, zoneId: ZoneId): Boolean {
    val beforeDate = before.transaction.date
    val afterDate = after.transaction.date

    return isSameGroup(beforeDate, afterDate, zoneId)
  }

  private fun isSameGroup(before: Instant, after: Instant, zoneId: ZoneId): Boolean =
    before.year(zoneId) == after.year(zoneId) &&
      before.dayOfYear(zoneId) == after.dayOfYear(zoneId) &&
      before.month(zoneId) == after.month(zoneId)

  private fun Instant.year(zoneId: ZoneId) = toLocalDateTime(TimeZone.of(zoneId.id)).year
  private fun Instant.dayOfYear(zoneId: ZoneId) = toLocalDateTime(TimeZone.of(zoneId.id)).dayOfYear
  private fun Instant.month(zoneId: ZoneId) = toLocalDateTime(TimeZone.of(zoneId.id)).month

  private fun getTransactionsFlow(): Flow<PagingData<TransactionModel>> {
    val arguments = args ?: return transactionRepository.getTransactionsPagingFlow(viewModelScope)

    return when (arguments) {
      is TransactionsListArgs.TransactionListArgsByCategory ->
        transactionRepository.getTransactionsPagedInPeriod(arguments.categoryId, arguments.from, arguments.to)

      is TransactionsListArgs.TransactionListArgsByType ->
        transactionRepository.getTransactionsPagedInPeriod(arguments.transactionType, arguments.from, arguments.to)

      is TransactionsListArgs.TransactionListArgsBySubcategory ->
        transactionRepository.getTransactionsInSubcategoryPagedInPeriod(
          targetSubcategoryId = arguments.subcategoryId,
          from = arguments.from,
          to = arguments.to,
        )
    }
  }

  fun getTransactionDetailsRoute(transactionModel: TransactionModel, editMode: Boolean): String =
    createTransactionScreenApi.getRoute(
      source = transactionModel.source,
      target = transactionModel.target,
      payload = CreateTransactionEventPayload(
        transactionId = transactionModel.id,
        note = transactionModel.note,
        date = transactionModel.date,
        transactionAmount = transactionModel.amount.abs(amountFormatter),
        transactionType = transactionModel.type.id,
        transferReceivedAmount = transactionModel.transferReceivedAmount,
        editMode = editMode,
      ),
    )

  fun getCreateTransactionRoute(): String = createTransactionScreenApi.getRoute(source = null, target = null)
}

private fun TransactionsListViewModel.transactionUiState(
  transactionRepository: TransactionRepository,
  pagingList: Flow<PagingData<TransactionsListViewModel.UiModel>>,
): Flow<NetworkViewState<TransactionsUiState>> {
  return transactionRepository.getTransactionsPagingFlow(viewModelScope).asResult().map {
    when (it) {
      is Result.Loading -> NetworkViewState.Loading
      is Result.Error -> NetworkViewState.Error(resourceValueOf(R.string.transactions_list_error_title))
      is Result.Success -> {
        NetworkViewState.Success(TransactionsUiState(pagingList))
      }

      is Result.Idle -> NetworkViewState.Idle
    }
  }
}