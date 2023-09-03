package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.database.model.AccountEntity
import java.math.RoundingMode
import java.text.DecimalFormat

data class Account constructor(
  val id: Long = 0,
  val name: String,
  val balance: Double,
  val currencyModel: CurrencyModel,
  val icon: AccountIconModel,
  val color: ColorModel,
) {

  val decimalFormat = DecimalFormat("#.##").apply {
    roundingMode = RoundingMode.HALF_UP
  }

  val formattedBalance: String
    get() = decimalFormat.format(balance) + " " + currencyModel.currencySymbol
}

fun AccountEntity.toExternalModel(): Account = with(this) {
  val currencyModel = CurrencyModel.getById(currencyId)
  Account(
    id = id,
    name = name,
    balance = balance,
    currencyModel = currencyModel,
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
