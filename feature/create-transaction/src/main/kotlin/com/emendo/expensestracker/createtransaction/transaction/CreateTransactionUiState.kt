package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.app.common.ext.updateIfType
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TextValue
import com.emendo.expensestracker.core.data.model.transaction.TransactionElement
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface CreateTransactionUiState {
  data object Loading : CreateTransactionUiState
  data class Error(val message: String) : CreateTransactionUiState
  data object Empty : CreateTransactionUiState
  data class DisplayTransactionData(
    val screenData: CreateTransactionScreenData,
    val target: TransactionItemModel,
    val source: TransactionItemModel?,
  ) : CreateTransactionUiState
}

@Stable
data class CreateTransactionScreenData(
  val amount: String,
  val transactionType: TransactionType,
  val amountError: StateEvent = consumed,
  val sourceError: StateEvent = consumed,
  val navigateUp: StateEvent = consumed,
)

val CreateTransactionUiState.successValue: CreateTransactionUiState.DisplayTransactionData?
  get() = (this as? CreateTransactionUiState.DisplayTransactionData)

fun TransactionElement.toTransactionItemModel() =
  TransactionItemModel(id, icon, name, color)

@Stable
data class TransactionItemModel(
  val id: Long,
  val icon: IconModel,
  val name: TextValue,
  val color: ColorModel,
)

internal fun MutableStateFlow<CreateTransactionUiState>.updateIfSuccess(
  function: (CreateTransactionUiState.DisplayTransactionData) -> CreateTransactionUiState.DisplayTransactionData,
) {
  updateIfType<CreateTransactionUiState.DisplayTransactionData, CreateTransactionUiState> { state ->
    function(state)
  }
}

internal fun MutableStateFlow<CreateTransactionUiState>.updateScreenData(
  function: (CreateTransactionScreenData) -> CreateTransactionScreenData,
) {
  updateIfSuccess { state ->
    state.copy(screenData = function(state.screenData))
  }
}
