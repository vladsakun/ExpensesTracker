package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.database.model.TransactionEntity
import com.emendo.expensestracker.core.database.model.TransactionFull
import com.emendo.expensestracker.core.database.model.TransactionType
import com.emendo.expensestracker.core.database.model.TransactionType.Companion.id
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

fun TransactionModel.asEntity() = TransactionEntity(
  sourceId = source.id,
  targetId = target.id,
  value = value,
  currencyId = currencyModel.id,
  type = type.id,
)

fun TransactionFull.asExternalModel(amountFormatter: AmountFormatter) = TransactionModel(
  id = id,
  source = source.asExternalModel(amountFormatter),
  target = targetAccount?.asExternalModel(amountFormatter)?.let { Target.Account(it) }
    ?: targetCategory?.asExternalModel()?.let { Target.Category(it) }
    ?: throw IllegalStateException("Transaction must have target"),
  formattedValue = amountFormatter.format(value),
  value = value,
  currencyModel = CurrencyModel.getById(currencyId),
  type = TransactionType.getById(type),
)

sealed interface Target {
  val id: Long
  val name: String
  val icon: IconModel

  class Account(account: AccountModel) : Target {
    override val id: Long = account.id
    override val name: String = account.name
    override val icon: IconModel = account.icon
  }

  class Category(category: CategoryModel) : Target {
    override val id: Long = category.id
    override val name: String = category.name
    override val icon: IconModel = category.icon
  }
}