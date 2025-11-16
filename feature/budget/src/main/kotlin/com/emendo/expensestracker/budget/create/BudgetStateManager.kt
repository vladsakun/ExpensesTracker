package com.emendo.expensestracker.budget.create

import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.model.ui.NetworkViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface BudgetStateManager<T> {
  val _state: MutableStateFlow<NetworkViewState<CreateBudgetScreenData>>
  val state: StateFlow<NetworkViewState<CreateBudgetScreenData>>

  fun updateName(name: String)
  fun updateLimit(limit: Amount)
  fun updateIcon(iconId: Int)
  fun updateColor(colorId: Int)
  fun updateCategory(category: CategoryModel)
  fun updateCurrency(amountFormatter: AmountFormatter, currency: CurrencyModel)
  fun updateConfirmEnabled(enabled: Boolean)
  fun requireDataValue(): T
}

