package com.emendo.expensestracker.accounts.detail

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TextValue
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.model.data.CurrencyModel
import java.math.BigDecimal

internal class AccountDetailPreviewData : PreviewParameterProvider<AccountDetailScreenData> {
  val data = AccountDetailScreenData.getDefaultState(
    accountModel = AccountModel(
      id = 1,
      currency = CurrencyModel(
        currencyCode = "USD",
        currencyName = "US dollar",
        currencySymbol = null,
      ),
      name = TextValue.Value("Bank"),
      icon = IconModel.EDUCATION,
      color = ColorModel.Blue,
      balance = BigDecimal.valueOf(122345, 2),
      balanceFormatted = "$1223.45",
    ),
  )
  override val values: Sequence<AccountDetailScreenData> = sequenceOf(data)
}