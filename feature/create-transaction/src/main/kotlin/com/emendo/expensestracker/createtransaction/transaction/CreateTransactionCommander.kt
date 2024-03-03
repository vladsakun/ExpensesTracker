package com.emendo.expensestracker.createtransaction.transaction

/**
 *  onSourceAmountClick = remember { { viewModel.showCalculatorBottomSheet() } },
 *       onTargetAmountClick = remember { { viewModel.showCalculatorBottomSheet(sourceTrigger = false) } },
 * onAccountClick = remember { viewModel::openAccountListScreen },
 *       onTransferTargetAccountClick = remember { viewModel::selectTransferTargetAccount },
 *       onCreateTransactionClick = remember { viewModel::saveTransaction },
 *       onBackPressed = remember { { navigator.navigateUp() } },
 *       onErrorConsumed = remember { viewModel::consumeFieldError },
 *       onConsumedNavigateUpEvent = remember { viewModel::consumeCloseEvent },
 *       onConsumedShowCalculatorBottomSheetEvent = remember { viewModel::consumeShowCalculatorBottomSheet },
 *       onConsumedHideCalculatorBottomSheetEvent = remember { viewModel::consumeHideCalculatorBottomSheet },
 *       onTransactionTypeChange = remember { viewModel::changeTransactionType },
 *       onNoteValueChange = remember { viewModel::updateNoteText },
 *       onDeleteClick = remember { viewModel::showConfirmDeleteTransactionBottomSheet },
 *       onDuplicateClick = remember { viewModel::duplicateTransaction },
 *       hideCalculatorBottomSheet = remember { viewModel::hideCalculatorBottomSheet },
 *
 */
interface CreateTransactionCommander {
  fun showCalculatorBottomSheet(sourceTrigger: Boolean)
  fun openAccountListScreen()
  fun selectTransferTargetAccount()
  fun saveTransaction()
  fun consumeFieldError(field: FieldWithError)
  fun consumeCloseEvent()
  fun consumeShowCalculatorBottomSheet()
  fun consumeHideCalculatorBottomSheet()
  fun changeTransactionType()
  fun updateNoteText(newNote: String)
  fun showConfirmDeleteTransactionBottomSheet()
  fun duplicateTransaction()
  fun hideCalculatorBottomSheet()

  fun proceedCommand(command: CreateTransactionCommand) {
    command.execute(this)
  }
}

interface CreateTransactionCommand {
  fun execute(commander: CreateTransactionCommander)
}

class ShowCalculatorBottomSheetCommand(private val sourceTrigger: Boolean = true) : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.showCalculatorBottomSheet(sourceTrigger)
  }
}

class OpenAccountListScreenCommand : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.openAccountListScreen()
  }
}

class SelectTransferTargetAccountCommand : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.selectTransferTargetAccount()
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

class ConsumeCloseEventCommand : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.consumeCloseEvent()
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

class ChangeTransactionTypeCommand : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.changeTransactionType()
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

class DuplicateTransactionCommand : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.duplicateTransaction()
  }
}

class HideCalculatorBottomSheetCommand : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.hideCalculatorBottomSheet()
  }
}
