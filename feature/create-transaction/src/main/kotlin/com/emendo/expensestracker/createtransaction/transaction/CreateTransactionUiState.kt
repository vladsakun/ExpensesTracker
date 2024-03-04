package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.data.api.model.transaction.TransactionElement
import com.emendo.expensestracker.data.api.model.transaction.TransactionType
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Immutable
data class CreateTransactionUiState(
  val amount: Amount,
  val screenData: CreateTransactionScreenData,
  val target: TransactionItemModel?,
  val source: TransactionItemModel?,
  val note: String? = null,
  val transferReceivedAmount: Amount? = null,
  val isCustomTransferAmount: Boolean = false,
  val sourceAmountFocused: Boolean = false,
  val transferTargetAmountFocused: Boolean = false,
  val amountCalculatorHint: String = "",
  val transferReceivedCalculatorHint: String = "",
)

@Stable
data class CreateTransactionScreenData(
  val transactionType: TransactionType,
  val deleteEnabled: Boolean = false,
  val duplicateEnabled: Boolean = false,
  val amountError: StateEvent = consumed,
  val sourceError: StateEvent = consumed,
)

data class CreateTransactionBottomSheetState(
  val data: BottomSheetData? = null,
  val show: StateEvent = consumed,
  val hide: StateEvent = consumed,
)

internal fun TransactionElement.toTransactionItemModel() =
  TransactionItemModel(icon, name, color)

@Stable
data class TransactionItemModel(
  val icon: IconModel,
  val name: TextValue,
  val color: ColorModel,
)

internal fun MutableStateFlow<CreateTransactionUiState>.updateScreenData(
  function: (CreateTransactionScreenData) -> CreateTransactionScreenData,
) {
  update { state ->
    state.copy(screenData = function(state.screenData))
  }
}