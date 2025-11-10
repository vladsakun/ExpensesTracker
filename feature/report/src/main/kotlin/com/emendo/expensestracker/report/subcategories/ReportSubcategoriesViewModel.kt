package com.emendo.expensestracker.report.subcategories

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aay.compose.barChart.model.BarParameters
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
import com.emendo.expensestracker.report.Period
import com.emendo.expensestracker.report.navArgs
import com.emendo.expensestracker.transactions.TransactionsListArgs
import com.emendo.expensestracker.transactions.TransactionsListScreenApi
import com.himanshoe.charty.bar.model.BarData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

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
  private val from: Instant
    get() = args.period.getFromInstant()
  private val to: Instant
    get() = args.period.getToInstant()

  internal val state: StateFlow<NetworkViewState<ReportSubcategoriesScreenData>> =
    uiState(
      userDataRepository = userDataRepository,
      transactionRepository = transactionRepository,
      args = args,
      amountFormatter = amountFormatter,
      convertCurrencyUseCase = convertCurrencyUseCase,
    ).stateInWhileSubscribed(viewModelScope, NetworkViewState.Loading)

  private val _navigationEvent: MutableStateFlow<StateEventWithContent<String>> = MutableStateFlow(consumed())
  internal val navigationEvent: StateFlow<StateEventWithContent<String>> = _navigationEvent

  override fun openAllTransactions() {
    val args = TransactionsListArgs.TransactionListArgsByCategory(
      categoryId = args.categoryId,
      from = from,
      to = to,
    )
    _navigationEvent.update { triggered(transactionsListApi.getRoute(args)) }
  }

  override fun openSubcategoryTransactions(subcategoryId: Long) {
    val args = TransactionsListArgs.TransactionListArgsBySubcategory(
      subcategoryId = subcategoryId,
      from = from,
      to = to,
    )
    _navigationEvent.update { triggered(transactionsListApi.getRoute(args)) }
  }

  internal fun onConsumedNavigationEvent() {
    _navigationEvent.update { consumed() }
  }
}

sealed interface BarChartGroup {
  data object ByDay : BarChartGroup
  data object ByWeek : BarChartGroup
  data object ByMonth : BarChartGroup
  data object ByYear : BarChartGroup
}

private fun uiState(
  userDataRepository: UserDataRepository,
  transactionRepository: TransactionRepository,
  amountFormatter: AmountFormatter,
  convertCurrencyUseCase: ConvertCurrencyUseCase,
  args: ReportSubcategoriesScreenNavArgs,
): Flow<NetworkViewState<ReportSubcategoriesScreenData>> {
  val transactionsFlow = transactionRepository.getTransactionsInPeriod(
    targetCategoryId = args.categoryId,
    from = args.period.getFromInstant(),
    to = args.period.getToInstant(),
  )

  return combine(userDataRepository.generalCurrency, transactionsFlow) { generalCurrency, transactions ->
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
        barData = getBarDataByWeek(
          transactions = transactions,
          generalCurrency = generalCurrency,
          convertCurrencyUseCase = convertCurrencyUseCase,
          amountFormatter = amountFormatter,
          //          barChartGroup = getBarChartGroupType(args.period),
          barChartGroup = BarChartGroup.ByWeek,
        ),
        barData2 = persistentListOf(
          BarParameters(
            dataName = "test",
            data = getBarDataByWeek(
              transactions = transactions,
              generalCurrency = generalCurrency,
              convertCurrencyUseCase = convertCurrencyUseCase,
              amountFormatter = amountFormatter,
              barChartGroup = BarChartGroup.ByWeek,
            ).map {
              it.yValue.toDouble()
            },
            barColor = Color.Red,
          )
        )
      )
    )
  }
}

fun getBarChartGroupType(period: Period): BarChartGroup {
  val to = period.getToInstant()
  val from = period.getFromInstant()

  val yearInMills = getDaysInMillis(365.0)
  val dayInMillis = getDaysInMillis(1.0)
  val weekInMillis = getDaysInMillis(7.0)
  val diff = to - from

  return when {
    diff > yearInMills -> BarChartGroup.ByYear
    diff > weekInMillis -> BarChartGroup.ByMonth
    diff > dayInMillis -> BarChartGroup.ByWeek
    else -> BarChartGroup.ByDay
  }
}

@OptIn(ExperimentalTime::class)
private fun getDaysInMillis(days: Double) =
  Duration.convert(days, DurationUnit.DAYS, DurationUnit.MILLISECONDS).toDuration(DurationUnit.MILLISECONDS)

fun getGroupedTransactions(
  transactions: List<TransactionModel>,
  barChartGroup: BarChartGroup,
): Map<Int, List<TransactionModel>> {
  if (transactions.isEmpty()) {
    return emptyMap()
  }

  return when (barChartGroup) {
    is BarChartGroup.ByDay -> groupByDayOfWeek(transactions)
    is BarChartGroup.ByWeek -> groupByWeekOfYear(transactions)
    is BarChartGroup.ByMonth -> groupByMonth(transactions)
    is BarChartGroup.ByYear -> groupByYear(transactions)
  }
}

private fun groupByDayOfWeek(transactions: List<TransactionModel>): Map<Int, List<TransactionModel>> {
  val grouped = transactions.groupBy {
    val date = it.date.toLocalDateTime(it.timeZone)
    date.dayOfWeek.value
  }

  // Fill missing days with empty lists (1 = Monday, 7 = Sunday)
  return (1..7).associateWith { dayOfWeek ->
    grouped[dayOfWeek] ?: emptyList()
  }
}

private fun groupByWeekOfYear(transactions: List<TransactionModel>): Map<Int, List<TransactionModel>> {
  val weekFields = WeekFields.of(Locale.getDefault())
  val grouped = transactions.groupBy { transaction ->
    val date = transaction.date.toLocalDateTime(transaction.timeZone).toJavaLocalDateTime()
    date.get(weekFields.weekOfYear())
  }

  // Fill missing weeks with empty lists (1-52)
  return (1..52).associateWith { week ->
    grouped[week] ?: emptyList()
  }
}

private fun groupByMonth(transactions: List<TransactionModel>): Map<Int, List<TransactionModel>> {
  val dates = transactions.map { it.date.toLocalDateTime(it.timeZone).toJavaLocalDateTime() }
  val minDate = dates.minOrNull() ?: return emptyMap()
  val maxDate = dates.maxOrNull() ?: return emptyMap()

  // Group by month within the date range
  val grouped = transactions.groupBy { transaction ->
    // Create a unique key for year-month combination
    val date = transaction.date.toLocalDateTime(transaction.timeZone)
    date.year * 12 + date.monthNumber
  }

  // Generate all months between min and max date
  val result = mutableMapOf<Int, List<TransactionModel>>()
  var currentDate = minDate.withDayOfMonth(1)
  var monthIndex = 1

  while (!currentDate.isAfter(maxDate.withDayOfMonth(1))) {
    val yearMonthKey = currentDate.year * 12 + currentDate.monthValue
    result[monthIndex] = grouped[yearMonthKey] ?: emptyList()

    currentDate = currentDate.plusMonths(1)
    monthIndex++
  }

  return result
}

private fun groupByYear(transactions: List<TransactionModel>): Map<Int, List<TransactionModel>> {
  val dates = transactions.map { it.date.toLocalDateTime(it.timeZone).toJavaLocalDateTime() }
  val minYear = dates.minOfOrNull { it.year } ?: return emptyMap()
  val maxYear = dates.maxOfOrNull { it.year } ?: return emptyMap()

  val grouped = transactions.groupBy { it.date.toLocalDateTime(it.timeZone).year }

  // Fill missing years with empty lists
  return (minYear..maxYear).mapIndexed { index, year ->
    (index + 1) to (grouped[year] ?: emptyList())
  }.toMap()
}

suspend fun getBarDataByWeek(
  transactions: List<TransactionModel>,
  generalCurrency: CurrencyModel,
  convertCurrencyUseCase: ConvertCurrencyUseCase,
  amountFormatter: AmountFormatter,
  barChartGroup: BarChartGroup,
): ImmutableList<BarData> {
  return getGroupedTransactions(transactions, barChartGroup)
    .map { (label, transactionsInWeek) ->
      val sum = getSum(transactionsInWeek, generalCurrency, convertCurrencyUseCase, amountFormatter)
      BarData(
        xValue = label,
        yValue = sum.value.toFloat(),
      )
    }
    .toImmutableList()
}

private fun getReportSumLabel(type: TransactionType) =
  if (type == TransactionType.EXPENSE) {
    resourceValueOf(R.string.report_all_expenses)
  } else {
    resourceValueOf(R.string.report_all_incomes)
  }

private suspend fun getSubcategories(
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

private suspend fun createSubcategoryUiModel(
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

private suspend fun getSum(
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
        usdToOriginalRate = it.usdToOriginalRate,
        conversionDate = it.date,
      )
    }
    .abs()

  return amountFormatter.format(sum, generalCurrency)
}