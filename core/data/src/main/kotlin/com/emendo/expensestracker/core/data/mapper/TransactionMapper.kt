package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.data.formatPositive
import com.emendo.expensestracker.core.data.mapper.base.Mapper
import com.emendo.expensestracker.core.database.model.transaction.TransactionFull
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.category.CategoryType.Companion.toTransactionType
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import kotlinx.datetime.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionMapper @Inject constructor(
  private val amountFormatter: AmountFormatter,
  private val accountMapper: AccountMapper,
) : Mapper<TransactionFull, TransactionModel> {

  override suspend fun map(from: TransactionFull): TransactionModel = with(from) {
    val sourceAccount: AccountModel = accountMapper.map(sourceAccount)

    val targetAccount: AccountModel? = targetAccount?.let { accountMapper.map(it) }
    val targetCategory: CategoryModel? = targetCategory?.asExternalModel()
    val target: TransactionTarget =
      targetAccount ?: targetCategory ?: throw IllegalStateException("Transaction must have a target")

    val currencyModel = CurrencyModel.toCurrencyModel(entity.currencyCode)
    val amount = amountFormatter.format(entity.value, currencyModel)

    val type = transactionType
    val isTransfer = type == TransactionType.TRANSFER
    val transferReceivedAmount = if (isTransfer) getTransferReceivedAmount() else null

    // Todo refactor
    return TransactionModel(
      id = entity.id,
      source = if (isTransfer) targetAccount!! else sourceAccount,
      target = if (isTransfer) sourceAccount else target,
      targetSubcategory = getTargetSubcategory(),
      amount = amount.formatPositive(),
      type = type,
      transferReceivedAmount = transferReceivedAmount?.formatPositive(),
      date = entity.date,
      note = entity.note,
      usdToOriginalRate = entity.usdToOriginalRate,
      timeZone = TimeZone.of(entity.timeZoneId),
    )
  }

  private fun TransactionFull.getTargetSubcategory(): CategoryModel? {
    val category = targetCategory ?: return null
    return targetSubcategory?.asExternalModel(category)
  }

  private fun TransactionFull.getTransferReceivedAmount(): Amount? {
    val transferReceivedValue = entity.transferReceivedValue
    val transferReceivedCurrency = entity.transferReceivedCurrencyCode?.let(CurrencyModel::toCurrencyModel)
    return if (transferReceivedValue != null && transferReceivedCurrency != null) {
      amountFormatter.format(transferReceivedValue, transferReceivedCurrency)
    } else {
      null
    }
  }
}

internal val TransactionFull.transactionType: TransactionType
  get() = when {
    targetAccount != null -> TransactionType.TRANSFER
    targetCategory != null -> CategoryType.getById(targetCategory!!.type).toTransactionType()
    else -> throw IllegalStateException("Can't get transaction type for transaction: $this")
  }