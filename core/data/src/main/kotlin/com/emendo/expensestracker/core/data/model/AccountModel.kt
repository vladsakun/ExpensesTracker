package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.app.resources.models.CalculatorTransactionUiModel
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.database.model.AccountEntity
import com.emendo.expensestracker.core.model.data.TransactionSource
import com.emendo.expensestracker.core.model.data.TransactionTarget
import java.math.BigDecimal

data class AccountModel(
  override val id: Long = 0,
  val name: String,
  val balance: BigDecimal,
  val currencyModel: CurrencyModel,
  val icon: IconModel,
  val color: ColorModel,
  val balanceFormatted: String = "",
) : TransactionSource, TransactionTarget

fun AccountEntity.asExternalModel(amountFormatter: AmountFormatter): AccountModel {
  val currencyModel = CurrencyModel.getById(currencyId)
  return AccountModel(
    id = id,
    name = name,
    balance = balance,
    balanceFormatted = amountFormatter.format(balance, currencyModel),
    currencyModel = currencyModel,
    icon = IconModel.getById(iconId),
    color = ColorModel.getById(colorId),
  )
}

fun AccountModel.asEntity(): AccountEntity {
  return AccountEntity(
    id = id,
    name = name,
    balance = balance,
    currencyId = currencyModel.id,
    iconId = icon.id,
    colorId = color.id,
  )
}

fun AccountModel.asTransactionUiModel(): CalculatorTransactionUiModel {
  return CalculatorTransactionUiModel(
    name = name,
    icon = icon.imageVector,
    element = this,
  )
}