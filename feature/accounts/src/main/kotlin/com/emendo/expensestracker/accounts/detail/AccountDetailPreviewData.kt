package com.emendo.expensestracker.accounts.detail

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.textValueOf

internal class AccountDetailPreviewData : PreviewParameterProvider<AccountDetailScreenData> {
  val data = getDefaultAccountDetailScreenState(
    accountModel = AccountModel(
      id = 1,
      currency = CurrencyModel(
        currencyCode = "USD",
        currencyName = "US dollar",
        currencySymbol = null,
      ),
      name = textValueOf("Bank"),
      icon = IconModel.EDUCATION,
      color = ColorModel.Blue,
      balance = Amount.Mock,
      ordinalIndex = 0,
    ),
  )
  override val values: Sequence<AccountDetailScreenData> = sequenceOf(data)
}