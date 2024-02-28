package com.emendo.expensestracker.accounts.common

import androidx.compose.runtime.Immutable
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.model.ui.ColorModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Immutable
data class AccountScreenData<T>(
  val name: String,
  val icon: IconModel,
  val color: ColorModel,
  val balance: Amount,
  val currency: CurrencyModel,
  val confirmEnabled: Boolean,
  val additionalData: T? = null,
)

@Immutable
sealed class UiState<T> {
  data class Loading<T>(val text: String = "") : UiState<T>()
  data class Data<T>(val data: T) : UiState<T>()
  data class Error<T>(val message: String) : UiState<T>()
}

fun <T> UiState<T>?.dataValue(): T? {
  return (this as? UiState.Data)?.data
}

fun <T> MutableStateFlow<UiState<T>>.updateData(
  function: (T) -> T,
) {
  update { state ->
    if (state is UiState.Data) {
      state.copy(data = function(state.data))
    } else {
      state
    }
  }
}