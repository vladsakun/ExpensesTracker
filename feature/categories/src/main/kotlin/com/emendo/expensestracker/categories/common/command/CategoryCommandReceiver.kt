package com.emendo.expensestracker.categories.common.command

import com.emendo.expensestracker.core.model.data.command.Command

typealias CategoryCommand = Command<CategoryCommandReceiver>

interface CategoryCommandReceiver {
  fun changeTitle(newTitle: String)

  fun processCommand(categoryCommand: CategoryCommand) {
    categoryCommand.execute(this)
  }
}

class UpdateTitleCategoryCommand(private val newTitle: String) : CategoryCommand {
  override fun execute(receiver: CategoryCommandReceiver) {
    receiver.changeTitle(newTitle)
  }
}