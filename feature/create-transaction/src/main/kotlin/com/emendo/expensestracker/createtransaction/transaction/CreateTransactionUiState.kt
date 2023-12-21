package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.app.common.ext.updateIfType
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TextValue
import com.emendo.expensestracker.core.data.model.transaction.TransactionElement
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import com.emendo.expensestracker.core.model.data.Amount
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface CreateTransactionUiState {
  data object Loading : CreateTransactionUiState
  data class DisplayTransactionData(
    val screenData: CreateTransactionScreenData,
    val target: TransactionItemModel?,
    val source: TransactionItemModel?,
    val note: String? = null,
    val transferReceivedAmount: Amount? = null,
    val isCustomTransferAmount: Boolean = false,
    val sourceAmountFocused: Boolean = false,
    val transferTargetAmountFocused: Boolean = false,
  ) : CreateTransactionUiState
}

@Stable
data class CreateTransactionScreenData(
  val amount: Amount,
  val transactionType: TransactionType,
  val deleteEnabled: Boolean = false,
  val duplicateEnabled: Boolean = false,
  val amountError: StateEvent = consumed,
  val sourceError: StateEvent = consumed,
  val navigateUp: StateEvent = consumed,
) {
  companion object {
    fun default(
      amount: Amount,
      transactionType: TransactionType,
    ) = CreateTransactionScreenData(
      amount = amount,
      transactionType = transactionType,
    )
  }
}

val CreateTransactionUiState.successValue: CreateTransactionUiState.DisplayTransactionData?
  get() = (this as? CreateTransactionUiState.DisplayTransactionData)

fun TransactionElement.toTransactionItemModel() =
  TransactionItemModel(icon, name, color)

@Stable
data class TransactionItemModel(
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