package com.emendo.expensestracker.createtransaction.transaction.domain

import com.emendo.expensestracker.core.domain.api.CreateTransactionController
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import java.math.BigDecimal
import javax.inject.Inject

class GetConvertedTransferAmountUseCase @Inject constructor(
  private val createTransactionController: CreateTransactionController,
  private val getConvertedFormattedValueUseCase: GetConvertedFormattedValueUseCase,
) {

  operator fun invoke(amount: BigDecimal): Amount? {
    val target: AccountModel = createTransactionController.getTargetSnapshot() as? AccountModel ?: return null
    val source: TransactionSource = createTransactionController.getSourceSnapshot() ?: return null

    return getConvertedFormattedValueUseCase(
      value = amount,
      fromCurrency = source.currency,
      toCurrency = target.currency,
    )
  }
}