package com.emendo.expensestracker.createtransaction.transaction

import com.emendo.expensestracker.core.domain.api.CreateTransactionController
import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import com.emendo.expensestracker.create.transaction.api.CreateTransactionScreenApi
import com.emendo.expensestracker.createtransaction.destinations.CreateTransactionScreenDestination
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class CreateTransactionScreenApiImpl @Inject constructor(
  private val createTransactionController: CreateTransactionController,
) : CreateTransactionScreenApi {

  override fun getRoute(
    source: TransactionSource?,
    target: TransactionTarget?,
    payload: CreateTransactionEventPayload?,
  ): String {

    with(createTransactionController) {
      target?.let(::setTarget)
      source?.let(::setSource)
      payload?.let(::setTransactionPayload)
    }

    return CreateTransactionScreenDestination.route
  }
}