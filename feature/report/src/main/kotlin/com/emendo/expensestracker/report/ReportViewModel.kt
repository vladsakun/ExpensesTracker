package com.emendo.expensestracker.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.common.NetworkViewState
import com.emendo.expensestracker.core.app.common.ext.stateFlow
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.manager.ExpeLocaleManager
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import com.emendo.expensestracker.data.api.repository.UserDataRepository
import com.emendo.expensestracker.model.ui.TextValue
import com.emendo.expensestracker.model.ui.resourceValueOf
import com.emendo.expensestracker.model.ui.textValueOf
import com.emendo.expensestracker.report.ReportScreenData.CategoryValue
import com.emendo.expensestracker.report.domain.GetCategoriesWithTotalTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import java.math.BigDecimal
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

// Todo add custom period
@HiltViewModel
class ReportViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  userDataRepository: UserDataRepository,
  private val transactionRepository: TransactionRepository,
  private val amountFormatter: AmountFormatter,
  private val getCategoriesWithTotalTransactionsUseCase: GetCategoriesWithTotalTransactionsUseCase,
  private val localeManager: ExpeLocaleManager,
) : ViewModel(), ReportScreenCommander {

  private val _transactionType: MutableStateFlow<TransactionType> by savedStateHandle.stateFlow(TransactionType.DEFAULT)

  // Todo move to savedStateHandle
  private val _selectedPeriod: MutableStateFlow<ReportPeriod> = MutableStateFlow(getDefaultPeriod())
  internal val selectedPeriod: StateFlow<ReportPeriod> = _selectedPeriod
  private val _showPickerDialog: MutableStateFlow<Boolean> by savedStateHandle.stateFlow(false)

  private val transactionsFlow: Flow<List<TransactionModel>> = _selectedPeriod.flatMapLatest { period ->
    val (from, to) = getFromTo(period)
    transactionRepository.getTransactionsInPeriod(from = from, to = to)
      .map { transactions ->
        transactions.filter { it.type == TransactionType.EXPENSE }
      }
  }

  // Todo move to savedStateHandle
  private var periodsCache: Pair<Locale, ImmutableList<ReportPeriod>?> = localeManager.getLocale() to null

  private fun getFromTo(period: ReportPeriod): Pair<Instant, Instant> = when (period) {
    is ReportPeriod.Date -> period.start to period.end
    is ReportPeriod.AllTime -> Instant.DISTANT_PAST to Instant.DISTANT_FUTURE
    is ReportPeriod.Custom -> checkNotNull(period.start) to checkNotNull(period.end)
  }

  internal val state: StateFlow<NetworkViewState<ReportScreenData>> =
    combine(
      userDataRepository.generalCurrency,
      transactionsFlow,
      _transactionType,
      _showPickerDialog,
    ) { currency, transactions, transactionType, showPickerDialog ->
      val categoryWithTotal: List<Pair<CategoryModel, BigDecimal>> =
        getCategoriesWithTotalTransactionsUseCase(transactions, transactionType)
          .sortedByDescending { it.second }

      val pieChartData: ImmutableList<ReportPieChartSlice> = categoryWithTotal.map { (category, total) ->
        ReportPieChartSlice(
          value = total.toFloat(),
          color = category.color,
        )
      }.toImmutableList()

      NetworkViewState.Success(
        ReportScreenData(
          pieChartData = pieChartData,
          balanceDate = "Balance: ", // Todo remove hardcoded string
          balance = amountFormatter.format(transactions.sumOf { it.amount.value }.abs(), currency),
          allExpenses = amountFormatter.format(transactions.sumOf { it.amount.value }.abs(), currency),
          categoryValues = categoryWithTotal.map { (category, total) ->
            CategoryValue(
              categoryId = category.id,
              icon = category.icon,
              categoryName = category.name,
              amount = amountFormatter.format(total, currency),
              color = category.color,
            )
          }.toImmutableList(),
          transactionType = transactionType,
          reportSumLabel = resourceValueOf(getReportSumLabelResId(transactionType)),
          periods = getPeriods(),
          showPickerDialog = showPickerDialog,
        )
      )
    }.stateInWhileSubscribed(viewModelScope, NetworkViewState.Loading)

  private suspend fun getPeriods(): ImmutableList<ReportPeriod> {
    val selectedPeriodValue = selectedPeriod.value
    if (periodsCache.second != null && localeManager.getLocale() == periodsCache.first && selectedPeriodValue !is ReportPeriod.Custom) {
      return periodsCache.second!!
    }

    val firstTransaction = transactionRepository.retrieveFirstTransaction()?.date ?: Clock.System.now()

    val now = getNow()
    val startYear = firstTransaction.toLocalDateTime(TimeZone.UTC).year
    val yearNow = now.year
    val diff = (yearNow - startYear).coerceAtMost(3) // Maximum 3 years history suggestion

    // Years
    val periods = mutableListOf(
      if (selectedPeriodValue is ReportPeriod.Custom) selectedPeriodValue else ReportPeriod.Custom(),
      ReportPeriod.AllTime()
    )
    for (i in yearNow downTo yearNow - diff) {
      val year = Year.of(i)
      val start = year.atDay(1).atStartOfDay().toInstant(TimeZone.UTC.toJavaZoneOffset()).toKotlinInstant()
      val end = year.atDay(year.length()).atStartOfDay().toInstant(TimeZone.UTC.toJavaZoneOffset()).toKotlinInstant()
      periods.add(
        ReportPeriod.Date(
          label = textValueOf(year.toString()),
          start = start,
          end = end,
        ),
      )
    }

    // Months
    for (i in yearNow downTo yearNow - diff) {
      val year = Year.of(i)
      val monthRange: OpenEndRange<Month> = if (year.value == now.year) {
        year.atMonth(now.month).month.rangeUntil(year.atMonth(1).month)
      } else {
        year.atMonth(12).month.rangeUntil(year.atMonth(1).month)
      }
      for (monthValue in monthRange.start.value downTo monthRange.endExclusive.value) {
        val month: java.time.Month = Month.of(monthValue)
        val date = LocalDateTime(year = year.value, month = month, 1, 0, 0)
        val (start, end) = firstAndLastDayOfMonth(date)
        periods.add(
          ReportPeriod.Date(
            label = getPeriodLabel(month, year),
            start = start,
            end = end,
          ),
        )
      }
    }

    val periodsImmutable = periods.toImmutableList()
    periodsCache = localeManager.getLocale() to periodsImmutable

    return periodsImmutable
  }

  private fun getPeriodLabel(month: java.time.Month, year: Year): TextValue.Value =
    textValueOf(month.getMonthLabel() + year.getYearLabel())

  private fun Int.getYearLabel(): String = Year.of(this).getYearLabel()
  private fun Year.getYearLabel(): String = " '${toString().substring(2)}"

  private fun java.time.Month.getMonthLabel(): String? =
    getDisplayName(TextStyle.FULL_STANDALONE, localeManager.getLocale())

  override fun setTransactionType(transactionType: TransactionType) {
    _transactionType.update { transactionType }
  }

  override fun setPeriod(period: ReportPeriod) {
    if (period is ReportPeriod.Custom) {
      _showPickerDialog.update { true }
      return
    }

    _selectedPeriod.update { period }
  }

  override fun hideDatePicker() {
    _showPickerDialog.update { false }
  }

  override fun selectCustomDate(selectedStartDateMillis: Long?, selectedEndDateMillis: Long?) {
    if (selectedStartDateMillis == null || selectedEndDateMillis == null) {
      return hideDatePicker()
    }

    fun getCustomLabel(localDateTime: LocalDateTime): String {
      val formatter = DateTimeFormatter.ofPattern("dd.MM.yy")
      return localDateTime.toJavaLocalDateTime().format(formatter)
    }

    val startInstant = Instant.fromEpochMilliseconds(selectedStartDateMillis)
    val startLocalDateTime = startInstant.toLocalDateTime(TimeZone.UTC)
    val endInstant = Instant.fromEpochMilliseconds(selectedEndDateMillis)
    val endLocalDateTime = endInstant.toLocalDateTime(TimeZone.UTC)

    val labelStart = getCustomLabel(startLocalDateTime)
    val labelEnd = getCustomLabel(endLocalDateTime)
    val label = textValueOf("$labelStart - $labelEnd")

    // Fix Custom is not selected after selecting custom period
    _selectedPeriod.update {
      ReportPeriod.Custom(
        label = label,
        start = startInstant,
        end = endInstant,
        selected = true,
      )
    }

    hideDatePicker()
  }

  private fun firstAndLastDayOfMonth(date: LocalDateTime): Pair<Instant, Instant> {
    val isLeapYear = Year.isLeap(date.year.toLong())
    val firstDayOfMonth = LocalDate(date.year, date.monthNumber, 1)
    val lastDayOfMonth = LocalDate(date.year, date.monthNumber, firstDayOfMonth.month.length(isLeapYear))

    // Fallback to UTC, because we don't care about exact time. We just need the start of day
    val firstInstant = firstDayOfMonth.atStartOfDayIn(TimeZone.UTC)
    // Todo think about nanoseconds
    val lastInstant = lastDayOfMonth.atTime(23, 59, 59).toInstant(TimeZone.UTC)

    return Pair(firstInstant, lastInstant)
  }

  private fun getDefaultPeriod(): ReportPeriod {
    val (start, end) = firstAndLastDayOfMonth(date = getNow())
    val localDateTime = start.toLocalDateTime(TimeZone.UTC)
    val month = localDateTime.month
    val year = Year.of(localDateTime.year)

    return ReportPeriod.Date(
      label = getPeriodLabel(month = month, year = year),
      start = start,
      end = end,
      selected = true,
    )
  }
}

private fun getNow() = Clock.System.now().toLocalDateTime(TimeZone.UTC)

private fun getReportSumLabelResId(transactionType: TransactionType): Int =
  if (transactionType == TransactionType.EXPENSE) R.string.report_all_expenses else R.string.report_all_incomes