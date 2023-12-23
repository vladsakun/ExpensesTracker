package com.emendo.expensestracker.transactions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEvent
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.common.ext.stateInLazily
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.manager.ExpeTimeZoneManager
import com.emendo.expensestracker.core.data.manager.cache.CurrencyCacheManager
import com.emendo.expensestracker.core.data.model.transaction.TransactionModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionType.Companion.id
import com.emendo.expensestracker.core.data.repository.api.TransactionRepository
import com.emendo.expensestracker.core.domain.currency.ConvertCurrencyUseCase
import com.emendo.expensestracker.core.domain.transaction.GetTransactionsSumUseCase
import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.*
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TransactionsListViewModel @Inject constructor(
  private val transactionRepository: TransactionRepository,
  timeZoneManager: ExpeTimeZoneManager,
  private val appNavigationEventBus: AppNavigationEventBus,
  private val amountFormatter: AmountFormatter,
  private val currencyCacheManager: CurrencyCacheManager,
  private val getTransactionsSumUseCase: GetTransactionsSumUseCase,
  private val convertCurrencyUseCase: ConvertCurrencyUseCase,
) : ViewModel() {
  private val repos: Flow<PagingData<UiModel.TransactionItem>> = transactionRepository
    .transactionsPagingFlow
    .map { it.map(UiModel::TransactionItem) }

  val list: Flow<PagingData<UiModel>> =
    combine(timeZoneManager.timeZoneState, repos) { zoneId: ZoneId, pagingData: PagingData<UiModel.TransactionItem> ->
      transformPagingData(pagingData, zoneId)
    }.cachedIn(viewModelScope)

  private fun transformPagingData(
    pagingData: PagingData<UiModel.TransactionItem>,
    zoneId: ZoneId,
  ): PagingData<UiModel> {
    return pagingData.insertSeparators { before: UiModel.TransactionItem?, after: UiModel.TransactionItem? ->
      if (after == null) {
        // we're at the end of the list
        return@insertSeparators null
      }

      if (before == null) {
        // we're at the beginning of the list
        return@insertSeparators SeparatorItem(zoneId, after)
      }

      // check between 2 items
      if (isSameGroup(before, after, zoneId)) {
        // no separator
        null
      } else {
        SeparatorItem(zoneId, after)
      }
    }
  }

  private suspend fun SeparatorItem(
    zoneId: ZoneId,
    after: UiModel.TransactionItem,
  ): UiModel.SeparatorItem {
    val timeZone = TimeZone.of(zoneId.id)
    val from = after.transaction.date.toLocalDateTime(timeZone).date.atStartOfDayIn(timeZone)
    val to = from.plus(DateTimePeriod(days = 1), timeZone)
    val generalCurrency = currencyCacheManager.getGeneralCurrencySnapshot()

    val transactions = transactionRepository.retrieveTransactionsInPeriod(from, to)
    val transactionWithConvertedValues = transactions.map { transaction ->
      if (transaction.currency == generalCurrency) {
        return@map transaction
      }

      val convertedValue = convertCurrencyUseCase.invoke(
        value = transaction.value,
        fromCurrencyCode = transaction.currency.currencyCode,
        toCurrencyCode = generalCurrency.currencyCode,
      )
      transaction.copy(value = convertedValue)
    }
    val total = getTransactionsSumUseCase(transactionWithConvertedValues)
    return UiModel.SeparatorItem(
      instant = after.transaction.date,
      sum = amountFormatter.format(total, generalCurrency).formattedValue,
    )
  }

  val state: StateFlow<TransactionScreenUiState> = transactionUiState(transactionRepository, list)
    .stateInLazily(
      scope = viewModelScope,
      initialValue = TransactionScreenUiState.DisplayTransactionsList(list),
    )

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

  private fun Instant.year(zoneId: ZoneId) =
    this.toLocalDateTime(TimeZone.of(zoneId.id)).year

  private fun Instant.dayOfYear(zoneId: ZoneId) =
    this.toLocalDateTime(TimeZone.of(zoneId.id)).dayOfYear

  private fun Instant.month(zoneId: ZoneId) =
    this.toLocalDateTime(TimeZone.of(zoneId.id)).month

  fun openTransactionDetails(transactionModel: TransactionModel) {
    appNavigationEventBus.navigate(
      AppNavigationEvent.CreateTransaction(
        source = transactionModel.source,
        target = transactionModel.target,
        payload = CreateTransactionEventPayload(
          transactionId = transactionModel.id,
          note = transactionModel.note,
          date = transactionModel.date,
          transactionAmount = transactionModel.amount,
          transactionType = transactionModel.type.id,
          transferReceivedAmount = transactionModel.transferReceivedAmount,
        ),
      )
    )
  }
}

private fun transactionUiState(
  transactionRepository: TransactionRepository,
  pagingList: Flow<PagingData<TransactionsListViewModel.UiModel>>,
): Flow<TransactionScreenUiState> {
  return transactionRepository.transactionsPagingFlow.asResult().map {
    when (it) {
      is Result.Loading -> TransactionScreenUiState.Loading
      is Result.Error -> TransactionScreenUiState.Error("Error loading transactions")
      is Result.Success -> TransactionScreenUiState.DisplayTransactionsList(pagingList)
      is Result.Empty -> TransactionScreenUiState.Empty
    }
  }
}