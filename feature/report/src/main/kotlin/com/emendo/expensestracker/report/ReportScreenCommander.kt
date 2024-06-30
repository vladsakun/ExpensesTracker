package com.emendo.expensestracker.report

import androidx.compose.runtime.Immutable
import com.emendo.expensestracker.core.model.data.TransactionType

interface ReportScreenCommander {

  fun setTransactionType(transactionType: TransactionType)
  fun setPeriod(period: ReportPeriod)

  fun proceedCommand(command: ReportScreenCommand) {
    command.execute(this)
  }
}

@Immutable
interface ReportScreenCommand {
  fun execute(commander: ReportScreenCommander)
}

class SetTransactionTypeCommand(private val transactionType: TransactionType) : ReportScreenCommand {
  override fun execute(commander: ReportScreenCommander) {
    commander.setTransactionType(transactionType)
  }
}

class SetPeriodCommand(private val period: ReportPeriod) : ReportScreenCommand {
  override fun execute(commander: ReportScreenCommander) {
    commander.setPeriod(period)
  }
}