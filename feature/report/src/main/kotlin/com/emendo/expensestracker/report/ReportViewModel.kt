package com.emendo.expensestracker.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.common.ext.stateFlow
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.manager.ExpeLocaleManager
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import com.emendo.expensestracker.data.api.repository.UserDataRepository
import com.emendo.expensestracker.data.api.utils.ExpeDateUtils
import com.emendo.expensestracker.model.ui.*
import com.emendo.expensestracker.report.ReportScreenData.CategoryValue
import com.emendo.expensestracker.report.destinations.ReportSubcategoriesRouteDestination
import com.emendo.expensestracker.report.domain.GetCategoriesWithTotalTransactionsUseCase
import com.emendo.expensestracker.transactions.TransactionsListArgs.TransactionListArgsByCategory
import com.emendo.expensestracker.transactions.TransactionsListArgs.TransactionListArgsByType
import com.emendo.expensestracker.transactions.TransactionsListScreenApi
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
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
  private val periodsFactory: PeriodsFactory,
  private val transactionsListScreenApi: TransactionsListScreenApi,
  private val categoryRepository: CategoryRepository,
) : ViewModel(), ReportScreenCommander {

  private val _selectedPeriod: MutableStateFlow<ReportPeriod> by savedStateHandle.stateFlow(getDefaultPeriod())
  internal val selectedPeriod: StateFlow<ReportPeriod> = _selectedPeriod

  private val _showPickerDialog: MutableStateFlow<Boolean> by savedStateHandle.stateFlow(false)
  internal val showPickerDialog: StateFlow<Boolean> = _showPickerDialog

  internal val periods: StateFlow<ImmutableList<ReportPeriod>> =
    flow { emit(getPeriods()) }
      .stateInWhileSubscribed(viewModelScope, persistentListOf(getDefaultPeriod()))

  internal val selectedPeriodIndex: StateFlow<Int> = combine(periods, selectedPeriod) { periods, selected ->
    periods.indexOf(selected).coerceAtLeast(0)
  }.stateInWhileSubscribed(viewModelScope, 0)

  private val _transactionType: MutableStateFlow<TransactionType> by savedStateHandle.stateFlow(TransactionType.DEFAULT)
  private val _selectedCategory: MutableStateFlow<Long?> by savedStateHandle.stateFlow(null)
  internal val selectedCategory: StateFlow<CategoryValue?> = _selectedCategory.map { id ->
    state.value.successData?.categoryValues?.find { it.categoryId == id }
  }.stateInWhileSubscribed(viewModelScope, null)

  private val transactionsFlow: Flow<List<TransactionModel>> =
    combine(getTransactionsInSelectedPeriod(), _transactionType) { transactions, type ->
      transactions.filter { it.type == type }
    }

  // Todo move to savedStateHandle
  private var periodsCache: Pair<Locale, ImmutableList<ReportPeriod>?> = localeManager.getLocale() to null

  private val _navigation: MutableStateFlow<ReportScreenNavigation> = MutableStateFlow(ReportScreenNavigation())
  internal val navigation: StateFlow<ReportScreenNavigation> = _navigation

  internal val state: StateFlow<NetworkViewState<ReportScreenData>> =
    combine(
      userDataRepository.generalCurrency,
      transactionsFlow,
      _transactionType,
    ) { currency, transactions, transactionType ->
      val categoryWithTotal: List<Pair<CategoryModel, BigDecimal>> =
        getCategoriesWithTotalTransactionsUseCase(transactions, transactionType)
          .sortedByDescending { it.second }

      val pieChartData = categoryWithTotal.map { (category, total) ->
        ReportPieChartSlice(
          categoryId = category.id,
          value = total.toFloat(),
          color = category.color,
        )
      }.toImmutableList().ifEmpty { null }

      NetworkViewState.Success(
        ReportScreenData(
          pieChartData = pieChartData,
          balanceDate = textValueOf("Balance: "), // Todo remove hardcoded string
          balance = amountFormatter.format(transactions.sumOf { it.amount.value }.abs(), currency),
          reportSum = ReportScreenData.ReportSum(
            label = resourceValueOf(getReportSumLabelResId(transactionType)),
            value = amountFormatter.format(transactions.sumOf { it.amount.value }.abs(), currency),
            type = transactionType,
          ),
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
          //          periods = getPeriods(), // Extract
        )
      )
    }.stateInWhileSubscribed(viewModelScope, NetworkViewState.Loading)

  private suspend fun getPeriods(): ImmutableList<ReportPeriod> {
    val selectedPeriodValue = selectedPeriod.value
    if (periodsCache.second != null && localeManager.getLocale() == periodsCache.first && selectedPeriodValue !is ReportPeriod.Custom) {
      return periodsCache.second!!
    }

    val periods = periodsFactory.getPeriods(selectedPeriodValue)
    periodsCache = localeManager.getLocale() to periods
    return periods
  }

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
      )
    }

    hideDatePicker()
  }

  override fun selectCategory(event: SelectCategoryClickEvent) {
    val categoryId = (event as? SelectCategoryClickEvent.CategorySelected)?.categoryId
    val period = selectedPeriod.value.getPeriod()
    val from = period.getFromInstant()
    val to = period.getToInstant()

    val route: String = if (categoryId == null) {
      transactionsListScreenApi.getRoute(TransactionListArgsByType(_transactionType.value, from, to))
    } else {
      if (categoryRepository.getCategorySnapshotById(categoryId)?.subcategories?.isEmpty() == true) {
        transactionsListScreenApi.getRoute(TransactionListArgsByCategory(categoryId, from, to))
      } else {
        ReportSubcategoriesRouteDestination(categoryId, period).route
      }
    }

    _navigation.update { it.copy(openCategoryTransactions = triggered(route)) }
  }

  override fun toggleSelectedPie(sliceId: Long?) {
    _selectedCategory.update { if (it == sliceId) null else sliceId }
  }

  fun onConsumedOpenCategoryTransactionsEvent() {
    _navigation.update { it.copy(openCategoryTransactions = consumed()) }
  }

  private fun getDefaultPeriod(): ReportPeriod {
    // Get from data store last selected period

    val (start, end) = ExpeDateUtils.getFirstAndLastDayOfMonth(date = getNow())
    val localDateTime = start.toLocalDateTime(TimeZone.UTC)
    val month = localDateTime.month
    val year = Year.of(localDateTime.year)

    return ReportPeriod.Date(
      label = periodsFactory.getPeriodLabel(month = month, year = year),
      start = start,
      end = end,
    )
  }

  private fun getTransactionsInSelectedPeriod(): Flow<List<TransactionModel>> =
    _selectedPeriod.flatMapLatest { selectedPeriod ->
      val period = selectedPeriod.getPeriod()
      transactionRepository.getTransactionsInPeriod(from = period.getFromInstant(), to = period.getToInstant())
    }
}

class PeriodsFactory @Inject constructor(
  private val localeManager: ExpeLocaleManager,
  private val transactionRepository: TransactionRepository,
) {

  suspend fun getPeriods(selectedPeriod: ReportPeriod): ImmutableList<ReportPeriod> {
    val firstTransaction = transactionRepository.retrieveFirstTransaction()?.date ?: Clock.System.now()

    val now = getNow()
    val startYear = firstTransaction.toLocalDateTime(TimeZone.UTC).year
    val yearNow = now.year
    val diff = (yearNow - startYear).coerceAtMost(3) // Maximum 3 years history suggestion

    // Years
    val periods = mutableListOf(
      if (selectedPeriod is ReportPeriod.Custom) selectedPeriod else ReportPeriod.Custom(),
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
        val (start, end) = ExpeDateUtils.getFirstAndLastDayOfMonth(date)
        periods.add(
          ReportPeriod.Date(
            label = getPeriodLabel(month, year),
            start = start,
            end = end,
          ),
        )
      }
    }

    return periods.reversed().toImmutableList()
  }

  fun getPeriodLabel(month: java.time.Month, year: Year): TextValue.Value =
    textValueOf(month.getMonthLabel() + year.getYearLabel())

  private fun Int.getYearLabel(): String = Year.of(this).getYearLabel()
  private fun Year.getYearLabel(): String = " '${toString().substring(2)}"

  private fun java.time.Month.getMonthLabel(): String? =
    getDisplayName(TextStyle.FULL_STANDALONE, localeManager.getLocale())

}

private fun getNow() = Clock.System.now().toLocalDateTime(TimeZone.UTC)

internal fun getReportSumLabelResId(transactionType: TransactionType): Int =
  if (transactionType == TransactionType.EXPENSE) R.string.report_all_expenses else R.string.report_all_incomes