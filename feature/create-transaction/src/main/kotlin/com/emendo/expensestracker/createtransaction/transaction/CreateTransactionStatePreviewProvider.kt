package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.textValueOf
import com.emendo.expensestracker.core.data.model.transaction.TransactionType

internal class CreateTransactionStatePreviewProvider : PreviewParameterProvider<CreateTransactionUiState> {

  val data = CreateTransactionUiState(
    screenData = CreateTransactionScreenData(
      amount = com.emendo.expensestracker.core.model.data.Amount.Mock,
      transactionType = TransactionType.DEFAULT,
    ),
    target = TransactionItemModel(IconModel.CHILDCARE, textValueOf("Childcare"), ColorModel.Base),
    source = TransactionItemModel(IconModel.CREDITCARD, textValueOf("Card"), ColorModel.Purple),
  )

  override val values: Sequence<CreateTransactionUiState> = sequenceOf(data)
}