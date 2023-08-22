package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.database.model.AccountEntity

data class Account constructor(
  val id: Long = 0,
  val name: String,
  val balance: Double,
  val currencyModel: CurrencyModel,
  val icon: AccountIconModel,
  val color: ColorModel,
)

fun AccountEntity.toExternalModel(): Account = with(this) {
  Account(
    id = id,
    name = name,
    balance = balance,
    currencyModel = CurrencyModel.getById(currencyId),
    icon = AccountIconModel.getById(iconId),
    color = ColorModel.getById(colorId),
  )
}

fun Account.asEntity(): AccountEntity = with(this) {
  AccountEntity(
    id = id,
    name = name,
    balance = balance,
    currencyId = currencyModel.id,
    iconId = icon.id,
    colorId = color.id,
  )
}
