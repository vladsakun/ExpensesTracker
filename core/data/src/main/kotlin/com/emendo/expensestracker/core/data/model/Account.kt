package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.database.model.AccountEntity

data class Account constructor(
  val id: Long = 0,
  val name: String,
  val balance: Double,
  val currency: Currency,
  val icon: AccountIconResource,
  val color: EntityColor,
)

fun AccountEntity.toExternalModel(): Account = with(this) {
  Account(
    id = id,
    name = name,
    balance = balance,
    currency = Currency.getById(currencyId),
    icon = AccountIconResource.getById(iconId),
    color = EntityColor.getById(colorId),
  )
}

fun Account.asEntity(): AccountEntity = with(this) {
  AccountEntity(
    id = id,
    name = name,
    balance = balance,
    currencyId = currency.id,
    iconId = icon.id,
    colorId = color.id,
  )
}
