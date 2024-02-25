package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.textValueOf
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.data.api.model.transaction.TransactionType

internal class CreateTransactionStatePreviewProvider : PreviewParameterProvider<CreateTransactionUiState> {

  val data = CreateTransactionUiState(
    amount = Amount.Mock,
    screenData = CreateTransactionScreenData(transactionType = TransactionType.DEFAULT),
    target = TransactionItemModel(IconModel.CHILDCARE, textValueOf("Childcare"), ColorModel.Base),
    source = TransactionItemModel(IconModel.CREDITCARD, textValueOf("Card"), ColorModel.Purple),
  )

  override val values: Sequence<CreateTransactionUiState> = sequenceOf(data)
}