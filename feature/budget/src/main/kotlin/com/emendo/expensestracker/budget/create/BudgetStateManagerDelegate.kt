package com.emendo.expensestracker.budget.create

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.NetworkViewState
import com.emendo.expensestracker.model.ui.successData
import com.emendo.expensestracker.model.ui.updateData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BudgetStateManagerDelegate(defaultState: NetworkViewState<CreateBudgetScreenData>? = null) :
  BudgetStateManager<CreateBudgetScreenData> {

  override val _state: MutableStateFlow<NetworkViewState<CreateBudgetScreenData>> =
    MutableStateFlow(defaultState ?: NetworkViewState.Loading)
  override val state: StateFlow<NetworkViewState<CreateBudgetScreenData>> = _state.asStateFlow()

  override fun updateName(name: String) {
    _state.updateData {
      it.copy(
        name = name,
        confirmButtonEnabled = it.limit.value != Amount.ZERO.value && it.categories.isNotEmpty(),
      )
    }
  }

  override fun updateLimit(limit: Amount) {
    _state.updateData {
      it.copy(
        limit = limit,
        confirmButtonEnabled = it.name.isNotBlank() && it.categories.isNotEmpty(),
      )
    }
  }

  override fun updateIcon(iconId: Int) {
    _state.updateData { it.copy(icon = IconModel.getById(iconId)) }
  }

  override fun updateColor(colorId: Int) {
    _state.updateData { it.copy(color = ColorModel.getById(colorId)) }
  }

  override fun updateCategories(categories: List<CategoryModel>) {
    _state.updateData {
      it.copy(
        confirmButtonEnabled = it.name.isNotBlank() && it.limit.value != Amount.ZERO.value && categories.isNotEmpty(),
        categories = categories
      )
    }
  }

  override fun updateCurrency(amountFormatter: AmountFormatter, currency: CurrencyModel) {
    _state.updateData {
      val formattedLimit = amountFormatter.format(it.limit.value, currency)
      it.copy(
        currency = currency,
        limit = formattedLimit,
      )
    }
  }

  override fun updateConfirmEnabled(enabled: Boolean) {
    _state.updateData { it.copy(confirmButtonEnabled = enabled) }
  }

  override fun requireDataValue(): CreateBudgetScreenData = _state.value.successData!!
}
