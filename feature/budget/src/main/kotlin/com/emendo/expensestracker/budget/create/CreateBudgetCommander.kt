package com.emendo.expensestracker.budget.create

import com.emendo.expensestracker.core.model.data.command.Command

typealias CreateBudgetCommand = Command<CreateBudgetCommander>

interface CreateBudgetCommander {
  fun changeName(newName: String)
  fun changeCategories(categoryIds: List<Long>)
  fun changeCurrency(currencyCode: String)
  fun changeIcon(iconId: Int)
  fun changeColor(colorId: Int)

  fun processCommand(command: CreateBudgetCommand) {
    command.execute(this)
  }
}

class UpdateNameBudgetCommand(private val newName: String) : CreateBudgetCommand {
  override fun execute(receiver: CreateBudgetCommander) {
    receiver.changeName(newName)
  }
}

class UpdateCategoriesBudgetCommand(private val categoryIds: List<Long>) : CreateBudgetCommand {
  override fun execute(receiver: CreateBudgetCommander) {
    receiver.changeCategories(categoryIds)
  }
}

class UpdateCurrencyBudgetCommand(private val currencyCode: String) : CreateBudgetCommand {
  override fun execute(receiver: CreateBudgetCommander) {
    receiver.changeCurrency(currencyCode)
  }
}

class UpdateIconBudgetCommand(private val iconId: Int) : CreateBudgetCommand {
  override fun execute(receiver: CreateBudgetCommander) {
    receiver.changeIcon(iconId)
  }
}