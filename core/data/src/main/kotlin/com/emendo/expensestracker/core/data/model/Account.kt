package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.database.model.AccountEntity
import java.math.RoundingMode
import java.text.DecimalFormat

data class Account constructor(
  val id: Long = 0,
  val name: String,
  val balance: Double,
  val currencyModel: CurrencyModel,
  val icon: IconModel,
  val color: ColorModel,
) {
  val formattedBalance: String
    get() = decimalFormat.format(balance) + " " + currencyModel.currencySymbol

  private val decimalFormat = DecimalFormat("#.##").apply {
    roundingMode = RoundingMode.HALF_UP
  }
}

fun AccountEntity.toExternalModel(): Account = with(this) {
  Account(
    id = id,
    name = name,
    balance = balance,
    currencyModel = CurrencyModel.getById(currencyId),
    icon = IconModel.getById(iconId),
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
