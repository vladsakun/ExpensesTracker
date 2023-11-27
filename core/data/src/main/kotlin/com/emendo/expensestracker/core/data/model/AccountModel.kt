package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TransactionElementName
import com.emendo.expensestracker.core.data.model.transaction.TransactionSource
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.database.model.AccountEntity
import com.emendo.expensestracker.core.model.data.CurrencyModel
import java.math.BigDecimal

data class AccountModel(
  override val id: Long = 0,
  override val currency: CurrencyModel,
  override val name: TransactionElementName,
  override val icon: IconModel,
  override val color: ColorModel,
  val balance: BigDecimal,
  val balanceFormatted: String = "",
) : TransactionSource, TransactionTarget

fun AccountModel.asEntity(): AccountEntity {
  return AccountEntity(
    id = id,
    name = (name as? TransactionElementName.Name)?.value ?: "",
    balance = balance,
    currencyCode = currency.currencyCode,
    iconId = icon.id,
    colorId = color.id,
  )
}