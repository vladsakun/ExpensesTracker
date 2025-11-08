package com.emendo.expensestracker.transactions.list

import com.emendo.expensestracker.core.model.data.command.Command
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel

interface TransactionListCommander {
  fun showConfirmDeleteTransactionBottomSheet(transaction: TransactionModel)

  fun proceedCommand(command: TransactionListCommand) {
    command.execute(this)
  }
}

internal typealias TransactionListCommand = Command<TransactionListCommander>

internal class ShowDeleteTransactionConfirmationBottomSheetCommand(
  private val transactionModel: TransactionModel,
) : TransactionListCommand {
  override fun execute(receiver: TransactionListCommander) {
    receiver.showConfirmDeleteTransactionBottomSheet(transactionModel)
  }
}
