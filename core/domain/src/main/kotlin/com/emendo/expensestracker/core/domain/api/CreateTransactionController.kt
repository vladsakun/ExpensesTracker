package com.emendo.expensestracker.core.domain.api

import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import com.emendo.expensestracker.data.api.model.transaction.TransactionType
import kotlinx.coroutines.flow.Flow

interface CreateTransactionController {
  fun getTarget(): Flow<TransactionTarget?>
  fun getTargetSnapshot(): TransactionTarget?
  fun setTarget(target: TransactionTarget?)

  fun getSource(): Flow<TransactionSource?>
  fun getSourceSnapshot(): TransactionSource?
  fun setSource(source: TransactionSource?)

  fun selectAccount(account: AccountModel)

  fun getDefaultTarget(transactionType: TransactionType): TransactionTarget

  fun isSelectMode(): Boolean
  fun finishSelectMode()

  fun startSelectSourceFlow()
  fun startSelectTransferTargetFlow()

  fun getTransactionPayload(): CreateTransactionEventPayload?
  fun setTransactionPayload(newPayload: CreateTransactionEventPayload)

  fun clear(shouldClearTarget: Boolean)
}