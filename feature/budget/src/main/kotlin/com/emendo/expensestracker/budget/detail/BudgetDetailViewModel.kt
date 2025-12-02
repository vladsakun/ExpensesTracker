package com.emendo.expensestracker.budget.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.budget.destinations.BudgetDetailRouteDestination
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.domain.budget.BudgetValueResult
import com.emendo.expensestracker.core.domain.budget.GetBudgetValueForDateUseCase
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.model.BudgetModel
import com.emendo.expensestracker.data.api.repository.BudgetRepository
import com.emendo.expensestracker.model.ui.NetworkViewState
import com.emendo.expensestracker.model.ui.TextValue
import com.emendo.expensestracker.model.ui.textValueOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

sealed class BudgetPeriod(open val label: TextValue) {
  data class Month(val yearMonth: YearMonth, override val label: TextValue) : BudgetPeriod(label)
}

private fun getMonthLabel(yearMonth: YearMonth): TextValue {
  val month = yearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
  val year = yearMonth.year.toString().takeLast(2)
  return textValueOf("$month '$year")
}

data class BudgetScreenData(
  val spent: String,
  val limit: String,
  val budget: BudgetModel,
  val progress: Float,
)

@HiltViewModel
class BudgetDetailViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val getBudgetValueForDateUseCase: GetBudgetValueForDateUseCase,
  private val amountFormatter: AmountFormatter,
  private val budgetRepository: BudgetRepository,
) : ViewModel() {

  private val _months: List<BudgetPeriod.Month> = (0..11).map {
    val ym = YearMonth.now().minusMonths(it.toLong())
    BudgetPeriod.Month(ym, getMonthLabel(ym))
  }.reversed()

  private val _periodsFlow = MutableStateFlow(_months)
  val periodsFlow: StateFlow<List<BudgetPeriod.Month>> = _periodsFlow

  private val _selectedPeriodIndex = MutableStateFlow(_months.indexOfFirst { it.yearMonth == YearMonth.now() })
  val selectedPeriodIndexFlow: StateFlow<Int> = _selectedPeriodIndex.asStateFlow()

  private val _isDeleted = MutableStateFlow(false)
  val isDeleted: StateFlow<Boolean> = _isDeleted

  // Get budgetId from navigation args via SavedStateHandle
  private val budgetId: Long by lazy(LazyThreadSafetyMode.NONE) {
    BudgetDetailRouteDestination.argsFrom(savedStateHandle).budgetId
  }

  // Budget value for selected period (separate flow)
  private val _budgetValueFlow: StateFlow<NetworkViewState<BudgetScreenData>> =
    _selectedPeriodIndex.flatMapLatest { period ->
      getBudgetValueForDateUseCase(budgetId, _months[period].yearMonth)
        .map<BudgetValueResult, NetworkViewState<BudgetScreenData>> { value ->
          val spent = value.spent
          val limit = value.limit
          val budget = value.budget
          val spentFormatted = amountFormatter.format(spent, budget.currency).formattedValue
          val limitFormatted = amountFormatter.format(limit, budget.currency).formattedValue
          val progress = if (limit > BigDecimal.ZERO) {
            (spent.toFloat() / limit.toFloat()).coerceIn(0f, 1f)
          } else {
            0f
          }
          NetworkViewState.Success(
            BudgetScreenData(
              spent = spentFormatted,
              limit = limitFormatted,
              progress = progress,
              budget = budget,
            )
          )
        }
    }.stateInWhileSubscribed(viewModelScope, NetworkViewState.Loading)
  val budgetValueFlow: StateFlow<NetworkViewState<BudgetScreenData>> = _budgetValueFlow

  fun setSelectedPeriodIndex(index: Int) {
    _selectedPeriodIndex.value = index
  }

  fun deleteBudget() {
    viewModelScope.launch {
      budgetRepository.deleteBudget(budgetId)
      _isDeleted.value = true
    }
  }
}
