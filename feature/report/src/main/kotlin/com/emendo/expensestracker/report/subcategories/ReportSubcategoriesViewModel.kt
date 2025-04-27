package com.emendo.expensestracker.report.subcategories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.domain.currency.ConvertCurrencyUseCase
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.extensions.asCategory
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import com.emendo.expensestracker.data.api.repository.UserDataRepository
import com.emendo.expensestracker.model.ui.NetworkViewState
import com.emendo.expensestracker.model.ui.resourceValueOf
import com.emendo.expensestracker.model.ui.textValueOrBlank
import com.emendo.expensestracker.report.navArgs
import com.emendo.expensestracker.transactions.TransactionsListArgs
import com.emendo.expensestracker.transactions.TransactionsListScreenApi
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ReportSubcategoriesViewModel @Inject constructor(
  transactionRepository: TransactionRepository,
  userDataRepository: UserDataRepository,
  amountFormatter: AmountFormatter,
  convertCurrencyUseCase: ConvertCurrencyUseCase,
  savedStateHandle: SavedStateHandle,
  private val transactionsListApi: TransactionsListScreenApi,
) : ViewModel(), ReportSubcategoriesCommander {

  private val args by lazy { savedStateHandle.navArgs<ReportSubcategoriesScreenNavArgs>() }
  private val transactionsFlow = transactionRepository.getTransactionsInPeriod(
    targetCategoryId = args.categoryId,
    from = args.period.getFromInstant(),
    to = args.period.getToInstant(),
  )

  internal val state: StateFlow<NetworkViewState<ReportSubcategoriesScreenData>> =
    uiState(userDataRepository, transactionsFlow, amountFormatter, convertCurrencyUseCase)
      .stateInWhileSubscribed(viewModelScope, NetworkViewState.Loading)

  private val _navigationEvent: MutableStateFlow<StateEventWithContent<String>> = MutableStateFlow(consumed())
  internal val navigationEvent: StateFlow<StateEventWithContent<String>> = _navigationEvent

  override fun openAllTransactions() {
    val args = TransactionsListArgs.TransactionListArgsByCategory(
      categoryId = args.categoryId,
      from = args.period.getFromInstant(),
      to = args.period.getToInstant()
    )
    _navigationEvent.update { triggered(transactionsListApi.getRoute(args)) }
  }

  override fun openSubcategoryTransactions(subcategoryId: Long) {
    val args = TransactionsListArgs.TransactionListArgsBySubcategory(
      subcategoryId = subcategoryId,
      from = args.period.getFromInstant(),
      to = args.period.getToInstant()
    )
    _navigationEvent.update { triggered(transactionsListApi.getRoute(args)) }
  }

  internal fun onConsumedNavigationEvent() {
    _navigationEvent.update { consumed() }
  }
}

private fun uiState(
  userDataRepository: UserDataRepository,
  transactionsFlow: Flow<List<TransactionModel>>,
  amountFormatter: AmountFormatter,
  convertCurrencyUseCase: ConvertCurrencyUseCase,
) = combine(userDataRepository.generalCurrency, transactionsFlow) { generalCurrency, transactions ->
  val firstTransaction =
    transactions.firstOrNull()
      ?: return@combine NetworkViewState.Error(resourceValueOf(R.string.report_subcategories_empty_state))

  val type = firstTransaction.type
  val category = firstTransaction.target.asCategory()

  NetworkViewState.Success(
    ReportSubcategoriesScreenData(
      categoryName = category.name,
      reportSumLabel = getReportSumLabel(type),
      reportSum = getSum(transactions, generalCurrency, convertCurrencyUseCase, amountFormatter),
      transactionType = type,
      color = category.color,
      subcategories = getSubcategories(
        transactions = transactions,
        generalCurrency = generalCurrency,
        convertCurrencyUseCase = convertCurrencyUseCase,
        amountFormatter = amountFormatter,
      ),
    ),
  )
}

private fun getReportSumLabel(type: TransactionType) =
  if (type == TransactionType.EXPENSE) {
    resourceValueOf(R.string.report_all_expenses)
  } else {
    resourceValueOf(R.string.report_all_incomes)
  }

private fun getSubcategories(
  transactions: List<TransactionModel>,
  generalCurrency: CurrencyModel,
  convertCurrencyUseCase: ConvertCurrencyUseCase,
  amountFormatter: AmountFormatter,
): ImmutableList<ReportSubcategoriesScreenData.SubcategoryUiModel> =
  transactions
    .groupBy { it.targetSubcategory }
    .mapNotNull { entry ->
      val subcategory = entry.key ?: return@mapNotNull null
      createSubcategoryUiModel(subcategory, entry.value, generalCurrency, convertCurrencyUseCase, amountFormatter)
    }
    .toImmutableList()

private fun createSubcategoryUiModel(
  subcategory: TransactionTarget,
  transactions: List<TransactionModel>,
  generalCurrency: CurrencyModel,
  convertCurrencyUseCase: ConvertCurrencyUseCase,
  amountFormatter: AmountFormatter,
) = ReportSubcategoriesScreenData.SubcategoryUiModel(
  id = subcategory.id,
  name = subcategory.name.textValueOrBlank(),
  icon = subcategory.icon,
  sum = getSum(transactions, generalCurrency, convertCurrencyUseCase, amountFormatter),
)

private fun getSum(
  transactions: List<TransactionModel>,
  generalCurrency: CurrencyModel,
  convertCurrencyUseCase: ConvertCurrencyUseCase,
  amountFormatter: AmountFormatter,
): Amount {
  val sum = transactions
    .sumOf {
      convertCurrencyUseCase(
        it.amount.value,
        fromCurrencyCode = it.amount.currency.currencyCode,
        toCurrencyCode = generalCurrency.currencyCode,
      )
    }
    .abs()

  return amountFormatter.format(sum, generalCurrency)
}