package com.emendo.expensestracker.accounts.create

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.emendo.expensestracker.core.model.data.CurrencyModel

internal class CreateAccountPreviewData : PreviewParameterProvider<CreateAccountScreenData> {
  val data = CreateAccountScreenData.getDefaultState(
    currency = CurrencyModel("EUR", "Euro", "€"),
  )
  override val values: Sequence<CreateAccountScreenData> = sequenceOf(data)
}