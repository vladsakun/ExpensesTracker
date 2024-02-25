package com.emendo.expensestracker.accounts.detail

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.textValueOf
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.data.api.model.AccountModel

internal class AccountDetailPreviewData : PreviewParameterProvider<AccountDetailScreenData> {
  val data = AccountDetailScreenData.getDefaultState(
    accountModel = object : AccountModel {
      override val id: Long = 1
      override val currency = CurrencyModel(
        currencyCode = "USD",
        currencyName = "US dollar",
        currencySymbol = null,
      )
      override val name = textValueOf("Bank")
      override val icon = IconModel.EDUCATION
      override val color = ColorModel.Blue
      override val balance = Amount.Mock
      override val ordinalIndex = 0
    }
  )
  override val values: Sequence<AccountDetailScreenData> = sequenceOf(data)
}