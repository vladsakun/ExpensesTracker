package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.model.TransactionModel
import com.emendo.expensestracker.core.data.model.TransactionTargetUiModel
import com.emendo.expensestracker.core.data.model.TransactionType
import com.emendo.expensestracker.core.data.model.asExternalModel
import com.emendo.expensestracker.core.data.repository.api.CurrencyRepository
import com.emendo.expensestracker.core.database.model.TransactionFull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionMapper @Inject constructor(
  private val amountFormatter: AmountFormatter,
  private val accountMapper: AccountMapper,
  private val currencyRepository: CurrencyRepository,
) : Mapper<TransactionFull, TransactionModel> {

  override suspend fun map(from: TransactionFull): TransactionModel = with(from) {
    val target = (targetAccount?.let { accountMapper.map(it) }?.let { TransactionTargetUiModel.Account(it) }
      ?: targetCategory?.asExternalModel()?.let { TransactionTargetUiModel.Category(it) }
      ?: throw IllegalStateException("Transaction must have target"))

    val type = target.transactionType
    val formattedValue = amountFormatter.format(transactionEntity.value)
    val formattedTransactionValue = if (type == TransactionType.INCOME) "+ $formattedValue" else formattedValue

    return TransactionModel(
      id = transactionEntity.id,
      source = accountMapper.map(sourceAccount),
      target = target,
      formattedValue = formattedTransactionValue,
      value = transactionEntity.value,
      currencyModel = currencyRepository.findCurrencyModel(transactionEntity.currencyCode),
      type = type,
      transferReceivedValue = transactionEntity.transferReceivedValue,
      transferCurrencyModel = transactionEntity.transferReceivedCurrencyCode?.run(currencyRepository::findCurrencyModel),
    )
  }
}