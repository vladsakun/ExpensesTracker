package com.emendo.expensestracker.createtransaction.transaction.domain

import com.emendo.expensestracker.core.domain.api.CreateTransactionController
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import com.emendo.expensestracker.data.api.model.transaction.TransactionType
import javax.inject.Inject

class GetTargetDefaultValueUseCase @Inject constructor(
  private val createTransactionController: CreateTransactionController,
) {

  operator fun invoke(transactionType: TransactionType): TransactionTarget =
    createTransactionController.getDefaultTarget(transactionType)
}