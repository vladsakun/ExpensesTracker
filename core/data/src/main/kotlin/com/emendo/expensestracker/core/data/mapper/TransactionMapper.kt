package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.isPositive
import com.emendo.expensestracker.core.data.mapper.base.Mapper
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType.Companion.toTransactionType
import com.emendo.expensestracker.core.data.model.category.asExternalModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import com.emendo.expensestracker.core.database.model.TransactionFull
import com.emendo.expensestracker.core.model.data.Amount
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
    val amount = amountFormatter.format(transactionEntity.value, currencyModel)

    val transferReceivedValue = transactionEntity.transferReceivedValue
    val transferReceivedCurrencyModel = transactionEntity.transferReceivedCurrencyCode?.let { currencyMapper.map(it) }
    val transferReceivedAmount =
      if (transferReceivedValue != null && transferReceivedCurrencyModel != null) {
        amountFormatter.format(transferReceivedValue, transferReceivedCurrencyModel)
      } else null

    // Todo refactor
    return TransactionModel(
      id = transactionEntity.id,
      source = accountMapper.map(sourceAccount),
      target = target,
      amount = amount.formatPositive(),
      type = type,
      transferReceivedAmount = transferReceivedAmount?.formatPositive(),
      date = transactionEntity.date,
      note = transactionEntity.note,
    )
  }

  private fun Amount.formatPositive(): Amount =
    copy(formattedValue = if (value.isPositive) "+$formattedValue" else formattedValue)
}