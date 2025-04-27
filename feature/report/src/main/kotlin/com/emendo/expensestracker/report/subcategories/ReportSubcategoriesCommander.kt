package com.emendo.expensestracker.report.subcategories

import com.emendo.expensestracker.core.model.data.command.Command

internal interface ReportSubcategoriesCommander {
  fun openAllTransactions()
  fun openSubcategoryTransactions(subcategoryId: Long)

  fun proceedCommand(command: ReportSubcategoriesCommand) {
    command.execute(this)
  }
}

internal typealias ReportSubcategoriesCommand = Command<ReportSubcategoriesCommander>

internal class OpenAllTransactionsCommand : ReportSubcategoriesCommand {
  override fun execute(receiver: ReportSubcategoriesCommander) {
    receiver.openAllTransactions()
  }
}

internal class OpenSubcategoryTransactionsCommand(private val subcategoryId: Long) : ReportSubcategoriesCommand {
  override fun execute(receiver: ReportSubcategoriesCommander) {
    receiver.openSubcategoryTransactions(subcategoryId)
  }
}