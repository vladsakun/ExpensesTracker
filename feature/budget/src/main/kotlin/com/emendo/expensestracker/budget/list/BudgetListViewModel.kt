package com.emendo.expensestracker.budget.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.domain.budget.GetBudgetProgressUseCase
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.model.BudgetModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class BudgetModelUi(
  val budget: BudgetModel,
  val spent: String,
  val limit: String,
  val percent: Float,
)

@HiltViewModel
class BudgetListViewModel @Inject constructor(
  private val getBudgetProgressUseCase: GetBudgetProgressUseCase,
  private val amountFormatter: AmountFormatter,
) : ViewModel() {
  val budgetsUi: StateFlow<List<BudgetModelUi>> = getBudgetProgressUseCase.getAllBudgetsProgress()
    .map { progressList ->
      progressList.map { progress ->
        BudgetModelUi(
          budget = progress.budget,
          spent = amountFormatter.format(progress.spent, progress.currency).formattedValue,
          limit = amountFormatter.format(progress.limit, progress.currency).formattedValue,
          percent = progress.percent
        )
      }
    }
    .stateInWhileSubscribed(viewModelScope, emptyList())
}
