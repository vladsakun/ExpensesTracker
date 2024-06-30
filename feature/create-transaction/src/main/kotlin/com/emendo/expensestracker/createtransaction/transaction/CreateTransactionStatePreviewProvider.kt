package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.textValueOf
import kotlinx.collections.immutable.persistentListOf

internal class CreateTransactionStatePreviewProvider : PreviewParameterProvider<CreateTransactionUiState> {

  val data = CreateTransactionUiState(
    amount = Amount.Mock,
    screenData = CreateTransactionScreenData(transactionType = TransactionType.DEFAULT),
    target = TransactionItemModel(IconModel.CHILDCARE, textValueOf("Childcare"), ColorModel.Base),
    source = TransactionItemModel(IconModel.CREDITCARD, textValueOf("Card"), ColorModel.Purple),
    accounts = persistentListOf(),
  )

  override val values: Sequence<CreateTransactionUiState> = sequenceOf(data)
}