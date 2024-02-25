package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.textValueOf
import com.emendo.expensestracker.core.data.mapper.base.Mapper
import com.emendo.expensestracker.core.database.model.AccountEntity
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.model.AccountModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountMapper @Inject constructor(
  private val amountFormatter: AmountFormatter,
) : Mapper<AccountEntity, AccountModel> {

  override suspend fun map(from: AccountEntity): AccountModel = with(from) {
    val currencyModel = CurrencyModel.toCurrencyModel(currencyCode)
    AccountModel(
      id = id,
      name = textValueOf(name),
      balance = amountFormatter.format(balance, currencyModel),
      currency = currencyModel,
      icon = IconModel.getById(iconId),
      color = ColorModel.getById(colorId),
      ordinalIndex = ordinalIndex,
    )
  }
}