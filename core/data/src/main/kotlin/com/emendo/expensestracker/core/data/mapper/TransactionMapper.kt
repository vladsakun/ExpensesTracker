package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.mapper.base.Mapper
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType.Companion.toTransactionType
import com.emendo.expensestracker.core.data.model.category.asExternalModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import com.emendo.expensestracker.core.database.model.TransactionFull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionMapper @Inject constructor(
  private val amountFormatter: AmountFormatter,
  private val accountMapper: AccountMapper,
  private val currencyMapper: CurrencyMapper,
) : Mapper<TransactionFull, TransactionModel> {

  override suspend fun map(from: TransactionFull): TransactionModel = with(from) {
    val targetAccount: AccountModel? = targetAccount?.let { accountMapper.map(it) }
    val targetCategory: CategoryModel? = targetCategory?.let(::asExternalModel)

    val target = targetAccount ?: targetCategory ?: throw IllegalStateException("Transaction must have a target")

    val type = when {
      targetAccount != null -> TransactionType.TRANSFER
      targetCategory != null -> targetCategory.type.toTransactionType()
      else -> TransactionType.DEFAULT
    }
    val currencyModel = currencyMapper.map(transactionEntity.currencyCode)
    val formattedValue = amountFormatter.format(transactionEntity.value, currencyModel)
    val formattedTransactionValue = if (type == TransactionType.INCOME) "+$formattedValue" else formattedValue

    return TransactionModel(
      id = transactionEntity.id,
      source = accountMapper.map(sourceAccount),
      target = target,
      formattedValue = formattedTransactionValue,
      value = transactionEntity.value,
      type = type,
      transferReceivedValue = transactionEntity.transferReceivedValue,
      transferCurrencyModel = transactionEntity.transferReceivedCurrencyCode?.run { currencyMapper.map(this) },
      date = transactionEntity.date,
      note = transactionEntity.note,
    )
  }
}