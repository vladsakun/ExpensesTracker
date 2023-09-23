package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.database.model.TransactionEntity
import com.emendo.expensestracker.core.database.model.TransactionType
import com.emendo.expensestracker.core.database.model.TransactionType.Companion.id
import com.emendo.expensestracker.core.model.data.TransactionSource
import com.emendo.expensestracker.core.model.data.TransactionTarget
import java.math.BigDecimal

data class TransactionModel(
  val id: Long,
  val source: TransactionSource,
  val target: TransactionTarget,
  val formattedValue: String,
  val value: BigDecimal,
  val currencyModel: CurrencyModel,
  val type: TransactionType,
)

fun TransactionModel.asEntity() = TransactionEntity(
  sourceId = source.id,
  targetId = target.id,
  value = value,
  currencyId = currencyModel.id,
  type = type.id,
)

//fun TransactionFull.asModel(amountFormatter: AmountFormatter) = TransactionModel(
//  id = id,
//  source = source,
//  target = targetAccount?.asModel() ?: targetCategory!!.asModel(),
//  formattedValue = amountFormatter.format(value),
//  value = value,
//  currencyModel = CurrencyModel.getById(currencyId),
//  type = TransactionType.getById(type),
//)