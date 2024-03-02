package com.emendo.expensestracker.categories.common.command

import com.emendo.expensestracker.core.model.data.command.Command

typealias CategoryCommand = Command<CategoryCommandReceiver>

interface CategoryCommandReceiver {
  fun changeTitle(newTitle: String)
  fun openSelectIconScreen()
  fun openSelectColorScreen()

  fun processCommand(categoryCommand: CategoryCommand) {
    categoryCommand.execute(this)
  }
}

class UpdateTitleCategoryCommand(private val newTitle: String) : CategoryCommand {
  override fun execute(receiver: CategoryCommandReceiver) {
    receiver.changeTitle(newTitle)
  }
}

class OpenSelectIconScreenCategoryCommand : CategoryCommand {
  override fun execute(receiver: CategoryCommandReceiver) {
    receiver.openSelectIconScreen()
  }
}

class OpenSelectColorScreenCategoryCommand : CategoryCommand {
  override fun execute(receiver: CategoryCommandReceiver) {
    receiver.openSelectColorScreen()
  }
}