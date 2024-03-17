package com.emendo.expensestracker.createtransaction.transaction.data

import androidx.compose.runtime.Immutable
import com.emendo.expensestracker.createtransaction.transaction.AccountUiModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionType

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
  fun saveTransaction()
  fun consumeFieldError(field: FieldWithError)
  fun consumeShowCalculatorBottomSheet()
  fun consumeHideCalculatorBottomSheet()
  fun updateTransactionType(transactionType: TransactionType)
  fun updateNoteText(newNote: String)
  fun showConfirmDeleteTransactionBottomSheet()
  fun hideCalculatorBottomSheet()
  fun selectAccount(account: AccountUiModel)

  fun proceedCommand(command: CreateTransactionCommand) {
    command.execute(this)
  }
}

@Immutable
interface CreateTransactionCommand {
  fun execute(commander: CreateTransactionCommander)
}

class ShowCalculatorBottomSheetCommand(private val sourceTrigger: Boolean = true) : CreateTransactionCommand {
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

class SelectAccount(private val account: AccountUiModel) : CreateTransactionCommand {
  override fun execute(commander: CreateTransactionCommander) {
    commander.selectAccount(account)
  }
}
