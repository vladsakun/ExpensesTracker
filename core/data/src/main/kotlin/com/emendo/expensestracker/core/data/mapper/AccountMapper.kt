package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.repository.api.CurrencyRepository
import com.emendo.expensestracker.core.database.model.AccountEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountMapper @Inject constructor(
  private val amountFormatter: AmountFormatter,
  private val currencyRepository: CurrencyRepository,
) : Mapper<AccountEntity, AccountModel> {

  override suspend fun map(from: AccountEntity): AccountModel = with(from) {
    val currencyModel = currencyRepository.findCurrencyModel(currencyCode)
    return AccountModel(
      id = id,
      name = name,
      balance = balance,
      balanceFormatted = amountFormatter.format(balance, currencyModel),
      currency = currencyModel,
      icon = IconModel.getById(iconId),
      color = ColorModel.getById(colorId),
    )
  }
}