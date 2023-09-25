package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.database.model.TransactionFull
import java.math.BigDecimal

data class TransactionModel(
  val id: Long,
  val source: AccountModel,
  val target: Target,
  val formattedValue: String,
  val value: BigDecimal,
  val currencyModel: CurrencyModel,
  val type: TransactionType,
)

fun TransactionFull.asExternalModel(amountFormatter: AmountFormatter): TransactionModel {
  val target = (targetAccount?.asExternalModel(amountFormatter)?.let { Target.Account(it) }
    ?: targetCategory?.asExternalModel()?.let { Target.Category(it) }
    ?: throw IllegalStateException("Transaction must have target"))

  return TransactionModel(
    id = transactionEntity.id,
    source = sourceAccount.asExternalModel(amountFormatter),
    target = target,
    formattedValue = amountFormatter.format(transactionEntity.value),
    value = transactionEntity.value,
    currencyModel = CurrencyModel.getById(transactionEntity.currencyId),
    type = TransactionType.getByTarget(target),
  )
}