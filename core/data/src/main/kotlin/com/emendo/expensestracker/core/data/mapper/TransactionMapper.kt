package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.data.formatPositive
import com.emendo.expensestracker.core.data.mapper.base.Mapper
import com.emendo.expensestracker.core.database.model.TransactionFull
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.category.CategoryType.Companion.toTransactionType
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionMapper @Inject constructor(
  private val amountFormatter: AmountFormatter,
  private val accountMapper: AccountMapper,
) : Mapper<TransactionFull, TransactionModel> {

  override suspend fun map(from: TransactionFull): TransactionModel = with(from) {
    val targetAccount: AccountModel? = targetAccount?.let { accountMapper.map(it) }
    val targetCategory: CategoryModel? = targetCategory?.let(::asExternalModel)
    val target = targetAccount ?: targetCategory ?: throw IllegalStateException("Transaction must have a target")

    val currencyModel = CurrencyModel.toCurrencyModel(transactionEntity.currencyCode)
    val amount = amountFormatter.format(transactionEntity.value, currencyModel)

    val transferReceivedValue = transactionEntity.transferReceivedValue
    val transferReceivedCurrency = transactionEntity.transferReceivedCurrencyCode?.let(CurrencyModel::toCurrencyModel)
    val transferReceivedAmount =
      if (transferReceivedValue != null && transferReceivedCurrency != null) {
        amountFormatter.format(transferReceivedValue, transferReceivedCurrency)
      } else {
        null
      }

    // Todo refactor
    return TransactionModel(
      id = transactionEntity.id,
      source = accountMapper.map(sourceAccount),
      target = target,
      amount = amount.formatPositive(),
      type = transactionType,
      transferReceivedAmount = transferReceivedAmount?.formatPositive(),
      date = transactionEntity.date,
      note = transactionEntity.note,
    )
  }
}

internal val TransactionFull.transactionType: TransactionType
  get() = when {
    targetAccount != null -> TransactionType.TRANSFER
    targetCategory != null -> CategoryType.getById(targetCategory!!.type).toTransactionType()
    else -> throw IllegalStateException("Can't get transaction type for transaction: $this")
  }