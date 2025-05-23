package com.emendo.expensestracker.createtransaction.transaction.data

import androidx.compose.runtime.Immutable
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.createtransaction.transaction.AccountUiModel
import com.emendo.expensestracker.createtransaction.transaction.SubcategoryUiModel

interface CreateTransactionCommander {
  fun showCalculatorBottomSheet(sourceTrigger: Boolean)
  fun saveTransaction()
  fun consumeFieldError(field: FieldWithError)
  fun consumeShowCalculatorBottomSheet()
  fun consumeHideCalculatorBottomSheet()
  fun updateTransactionType(transactionType: TransactionType)
  fun updateNoteText(newNote: String)
  fun showConfirmDeleteTransactionBottomSheet()
  fun hideCalculatorBottomSheet()
  fun selectSourceAccount(account: AccountUiModel)
  fun selectTargetAccount(account: AccountUiModel)
  fun selectSubcategory(subcategory: SubcategoryUiModel)

  fun proceedCommand(command: CreateTransactionCommand) {
    command.execute(this)
  }
}

@Immutable
interface CreateTransactionCommand {
  fun execute(commander: CreateTransactionCommander)
}

class ShowCalculatorBottomSheetCommand(private val sourceTrigger: Boolean) : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.showCalculatorBottomSheet(sourceTrigger)
  }
}

class SaveTransactionCommand : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.saveTransaction()
  }
}

class ConsumeFieldErrorCommand(private val field: FieldWithError) : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.consumeFieldError(field)
  }
}

class ConsumeShowCalculatorBottomSheetCommand : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.consumeShowCalculatorBottomSheet()
  }
}

class ConsumeHideCalculatorBottomSheetCommand : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.consumeHideCalculatorBottomSheet()
  }
}

class ChangeTransactionTypeCommand(private val transactionType: TransactionType) : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.updateTransactionType(transactionType)
  }
}

class UpdateNoteTextCommand(private val newNote: String) : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.updateNoteText(newNote)
  }
}

class ShowConfirmDeleteTransactionBottomSheetCommand : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.showConfirmDeleteTransactionBottomSheet()
  }
}

class HideCalculatorBottomSheetCommand : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.hideCalculatorBottomSheet()
  }
}

class SelectSubcategoryCommand(private val subcategory: SubcategoryUiModel) : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.selectSubcategory(subcategory)
  }
}