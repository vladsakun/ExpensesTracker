package com.emendo.expensestracker.categories.subcategory

import com.emendo.expensestracker.core.model.data.command.Command

typealias CreateSubcategoryCommand = Command<CreateSubcategoryCommander>

interface CreateSubcategoryCommander {
  fun changeTitle(newTitle: String)

  fun processCommand(categoryCommand: CreateSubcategoryCommand) {
    categoryCommand.execute(this)
  }
}

class UpdateTitleSubcategoryCommand(private val newTitle: String) : CreateSubcategoryCommand {
  override fun execute(receiver: CreateSubcategoryCommander) {
    receiver.changeTitle(newTitle)
  }
}