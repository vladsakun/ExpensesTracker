package com.emendo.expensestracker.accounts.create

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel

internal class CreateAccountPreviewData : PreviewParameterProvider<CreateAccountScreenData> {

  val data = getDefaultCreateAccountState(
    currency = CurrencyModel.USD,
    balance = Amount.Mock,
  )
  override val values: Sequence<CreateAccountScreenData> = sequenceOf(data)
}