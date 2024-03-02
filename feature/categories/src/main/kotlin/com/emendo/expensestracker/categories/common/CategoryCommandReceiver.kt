package com.emendo.expensestracker.categories.common

import com.emendo.expensestracker.core.model.data.command.Command

typealias CategoryCommand = Command<CategoryCommandReceiver>

interface CategoryCommandReceiver {
  val categoryScreenNavigator: CategoryScreenNavigator
  val stateManager: CategoryStateManager<*>

  fun processCommand(categoryCommand: CategoryCommand) {
    categoryCommand.execute(this)
  }
}

class UpdateTitleCategoryCommand(private val newTitle: String) : CategoryCommand {
  override fun execute(receiver: CategoryCommandReceiver) {
    receiver.stateManager.changeTitle(newTitle)
  }
}

class OpenSelectIconScreenCategoryCommand : CategoryCommand {
  override fun execute(receiver: CategoryCommandReceiver) {
    receiver.categoryScreenNavigator.openSelectIconScreen()
  }
}

class OpenSelectColorScreenCategoryCommand : CategoryCommand {
  override fun execute(receiver: CategoryCommandReceiver) {
    receiver.categoryScreenNavigator.openSelectColorScreen()
  }
}