package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.ui.bottomsheet.BottomScreenTransition
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ScreenWithModalBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.TransactionCalculatorBottomSheet
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.createtransaction.destinations.SelectCategoryScreenDestination
import com.emendo.expensestracker.createtransaction.transaction.data.*
import com.emendo.expensestracker.createtransaction.transaction.design.CreateTransactionRow
import com.emendo.expensestracker.createtransaction.transaction.design.TopBar
import com.emendo.expensestracker.createtransaction.transaction.design.TransactionElementRow
import com.emendo.expensestracker.createtransaction.transaction.design.additional.AdditionalAction
import com.emendo.expensestracker.createtransaction.transaction.design.amount.Amount
import com.emendo.expensestracker.createtransaction.transaction.design.amount.EditableAmount
import com.emendo.expensestracker.createtransaction.transaction.design.amount.TransferAmount
import com.emendo.expensestracker.createtransaction.transaction.design.transfer.TransferColumn
import com.emendo.expensestracker.createtransaction.transaction.design.transfer.TransferRow
import com.emendo.expensestracker.data.api.model.transaction.TransactionType
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.theapache64.rebugger.Rebugger
import de.palm.composestateevents.EventEffect
import de.palm.composestateevents.triggered
import kotlinx.coroutines.launch

internal val marginVertical = Dimens.margin_large_x
internal val marginHorizontal = Dimens.margin_large_x
internal const val ERROR_ANIMATION_DURATION_MILLIS = 500
internal const val TRANSFER_BLOCK_MIN_HEIGHT = 100
private const val CHEVRON_WIDTH = 12

@RootNavGraph(start = true)
@Destination(style = BottomScreenTransition::class)
@Composable
fun CreateTransactionScreen(
  navigator: DestinationsNavigator,
  viewModel: CreateTransactionViewModel = hiltViewModel(),
) {
  val uiState = viewModel.uiState.collectAsStateWithLifecycle()
  val bottomSheetState = viewModel.bottomSheetState.collectAsStateWithLifecycle()

  ScreenWithModalBottomSheet(
    stateManager = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    bottomSheetContent = { type -> BottomSheetContent(type) },
  ) {
    CreateTransactionContent(
      stateProvider = uiState::value,
      bottomSheetStateProvider = bottomSheetState::value,
      onBackPressed = remember { { navigator.navigateUp() } },
      onCategoryClick = remember { { navigator.navigate(SelectCategoryScreenDestination) } },
      commandProcessor = remember { viewModel::proceedCommand },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTransactionContent(
  stateProvider: () -> CreateTransactionUiState,
  bottomSheetStateProvider: () -> CreateTransactionBottomSheetState,
  onCategoryClick: () -> Unit,
  onBackPressed: () -> Unit,
  commandProcessor: (CreateTransactionCommand) -> Unit,
) {
  val scaffoldState = rememberBottomSheetScaffoldState()

  BottomSheetEffects(bottomSheetStateProvider, commandProcessor, scaffoldState)

  BottomSheetScaffold(
    scaffoldState = scaffoldState,
    topBar = { TopBar(stateProvider, commandProcessor, onBackPressed) },
    sheetContent = {
      val bottomSheetData = bottomSheetStateProvider().data
      if (bottomSheetData != null) {
        BottomSheetContent(bottomSheetData)
      }
    },
    // Workaround for issue https://issuetracker.google.com/issues/265444789
    sheetPeekHeight = 0.dp,
    modifier = Modifier
      .consumeWindowInsets(WindowInsets(0, 0, 0, 0))
      .fillMaxSize(),
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(paddingValues),
    ) {
      val state = stateProvider()

      Header(state, commandProcessor)
      if (state.screenData.transactionType != TransactionType.TRANSFER) {
        // Todo Extract to a common TransferBlock
        CategorySelection(state.target, onCategoryClick)

        // Todo extract to AccountSelection
        TransactionElementRow(
          transactionItem = state.source,
          label = stringResource(id = R.string.account),
          onClick = { commandProcessor(OpenAccountListScreenCommand()) },
          error = state.screenData.sourceError == triggered,
          onErrorConsumed = { commandProcessor(ConsumeFieldErrorCommand(FieldWithError.Source)) },
        )
        ExpeDivider()
      }
      // Todo think about commandProcessor passing
      NoteTextField(
        text = state.note,
        onNoteValueChange = { commandProcessor(UpdateNoteTextCommand(it)) },
        onFocused = { commandProcessor(HideCalculatorBottomSheetCommand()) },
      )
      ExpeDivider()
      SaveButton { commandProcessor(SaveTransactionCommand()) }
      AdditionalActions(commandProcessor)
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetEffects(
  bottomSheetStateProvider: () -> CreateTransactionBottomSheetState,
  commandProcessor: (CreateTransactionCommand) -> Unit,
  scaffoldState: BottomSheetScaffoldState,
) {
  val scope = rememberCoroutineScope()

  EventEffect(
    event = bottomSheetStateProvider().hide,
    onConsumed = { commandProcessor(ConsumeHideCalculatorBottomSheetCommand()) },
    action = {
      scope.launch {
        scaffoldState.bottomSheetState.partialExpand()
      }
    },
  )

  EventEffect(
    event = bottomSheetStateProvider().show,
    onConsumed = { commandProcessor(ConsumeShowCalculatorBottomSheetCommand()) },
    action = {
      scope.launch {
        scaffoldState.bottomSheetState.expand()
      }
    },
  )
}

@Composable
private inline fun Header(
  state: CreateTransactionUiState,
  crossinline commandProcessor: (CreateTransactionCommand) -> Unit,
) {
  if (state.screenData.transactionType == TransactionType.TRANSFER) {
    TransferBlock(state, commandProcessor)
  } else {
    IncomeExpenseBlock(state, commandProcessor)
  }
  ExpeDivider()
}

@Composable
private inline fun IncomeExpenseBlock(
  state: CreateTransactionUiState,
  crossinline commandProcessor: (CreateTransactionCommand) -> Unit,
) {
  val focusManager = LocalFocusManager.current

  Column(
    modifier = Modifier
      .clickable {
        commandProcessor(ShowCalculatorBottomSheetCommand())
        focusManager.clearFocus(force = true)
      },
  ) {
    // Todo fix Amount recomposition on note edit
    Rebugger(
      trackMap = mapOf(
        "formattedValue" to state.amount.formattedValue,
        "transactionType" to state.screenData.transactionType,
        "amountError" to state.screenData.amountError,
        "commandProcessor" to { commandProcessor(ConsumeFieldErrorCommand(FieldWithError.Amount)) },
      )
    )
    Amount(
      text = { state.amount.formattedValue },
      transactionType = { state.screenData.transactionType },
      error = state.screenData.amountError == triggered,
      onErrorConsumed = { commandProcessor(ConsumeFieldErrorCommand(FieldWithError.Amount)) },
    )
    EditableAmount(
      text = state.amountCalculatorHint,
      focused = state.sourceAmountFocused,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = marginHorizontal, vertical = Dimens.margin_small_xx),
    )
  }
}

@Composable
private inline fun TransferBlock(
  state: CreateTransactionUiState,
  crossinline commandProcessor: (CreateTransactionCommand) -> Unit,
) {
  TransferRow {
    TransferSource(state, commandProcessor)
    Chevron(
      width = CHEVRON_WIDTH.dp,
      height = TRANSFER_BLOCK_MIN_HEIGHT.dp,
    )
    TransferTarget(state, commandProcessor)
  }
}

@Composable
private inline fun RowScope.TransferSource(
  state: CreateTransactionUiState,
  crossinline commandProcessor: (CreateTransactionCommand) -> Unit,
) {
  TransferEntity(
    transactionItemModel = state.source,
    amountCommand = ShowCalculatorBottomSheetCommand(),
    commandProcessor = commandProcessor,
  ) {
    TransferAmount(text = state.amount.formattedValue)
    EditableAmount(
      text = state.amountCalculatorHint,
      focused = state.sourceAmountFocused,
    )
  }
}

@Composable
private inline fun RowScope.TransferTarget(
  state: CreateTransactionUiState,
  crossinline commandProcessor: (CreateTransactionCommand) -> Unit,
) {
  TransferEntity(
    transactionItemModel = state.target,
    commandProcessor = commandProcessor,
    amountCommand = ShowCalculatorBottomSheetCommand(false),
  ) {
    state.transferReceivedAmount?.let { amount ->
      TransferAmount(
        text = amount.formattedValue,
        textColor = transferTextColor(state),
      )
      EditableAmount(
        text = state.transferReceivedCalculatorHint,
        focused = state.transferTargetAmountFocused,
      )
    }
  }
}

@Composable
private inline fun RowScope.TransferEntity(
  transactionItemModel: TransactionItemModel?,
  amountCommand: CreateTransactionCommand,
  crossinline commandProcessor: (CreateTransactionCommand) -> Unit,
  crossinline amountBlock: @Composable ColumnScope.() -> Unit,
) {
  TransferColumn {
    if (transactionItemModel == null) {
      CreateAccountButton { commandProcessor(OpenAccountListScreenCommand()) }
      return
    }

    TransferAccount({ commandProcessor(SelectTransferTargetAccountCommand()) }, transactionItemModel)
    Column(
      modifier = Modifier.clickable(onClick = { commandProcessor(amountCommand) }),
    ) {
      amountBlock()
    }
  }
}

@Composable
private fun transferTextColor(state: CreateTransactionUiState) =
  if (state.isCustomTransferAmount) {
    MaterialTheme.customColorsPalette.successColor
  } else {
    MaterialTheme.colorScheme.outline
  }

@Composable
private fun AdditionalActions(commandProcessor: (CreateTransactionCommand) -> Unit) {
  Column {
    CreateTransactionRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(marginVertical),
    ) {
      AdditionalAction(
        titleResId = R.string.delete,
        icon = ExpeIcons.Delete,
        onClick = { commandProcessor(ShowConfirmDeleteTransactionBottomSheetCommand()) },
      )
      AdditionalAction(
        titleResId = R.string.duplicate,
        icon = ExpeIcons.FileCopy,
        onClick = { commandProcessor(DuplicateTransactionCommand()) },
      )
    }
  }
}

@Composable
private fun CategorySelection(
  category: TransactionItemModel?,
  onCategoryClick: () -> Unit,
) {
  TransactionElementRow(
    transactionItem = category,
    label = stringResource(id = R.string.category),
    onClick = onCategoryClick,
  )
}

@Composable
private fun SaveButton(
  onClick: () -> Unit,
) {
  ExpeButton(
    textResId = R.string.save_transaction,
    onClick = onClick,
    modifier = Modifier
      .padding(horizontal = marginHorizontal, vertical = marginVertical)
  )
}

@Composable
private fun NoteTextField(
  text: String?,
  onNoteValueChange: (String) -> Unit,
  onFocused: () -> Unit,
) {
  ExpeTextField(
    text = text,
    onValueChange = onNoteValueChange,
    modifier = Modifier
      .fillMaxWidth()
      .onFocusChanged { focusState ->
        if (focusState.isFocused) {
          onFocused()
        }
      },
    placeholder = stringResource(id = R.string.create_transaction_note_placeholder),
    paddingValues = PaddingValues(horizontal = marginHorizontal, vertical = marginVertical),
  )
}

@Composable
private fun CreateAccountButton(onClick: () -> Unit) {
  ExpeButton(
    textResId = R.string.create_account,
    onClick = onClick,
    colors = ButtonDefaults.outlinedButtonColors(),
    textStyle = MaterialTheme.typography.labelLarge,
  )
}

@Composable
private inline fun TransferAccount(
  noinline onClick: () -> Unit,
  account: TransactionItemModel,
) {
  Row(
    modifier = Modifier
      .heightIn(min = Dimens.icon_button_size)
      .clickable(
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
      )
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.End,
  ) {
    Icon(
      modifier = Modifier.size(Dimens.icon_size),
      imageVector = account.icon.imageVector,
      contentDescription = null,
    )
    Text(
      text = account.name.stringValue(),
      style = MaterialTheme.typography.bodyLarge,
    )
  }
}

@Composable
private fun ColumnScope.BottomSheetContent(sheetData: BottomSheetData) {
  when (sheetData) {
    is CalculatorBottomSheetData -> {
      val state = sheetData.state.collectAsStateWithLifecycle()

      TransactionCalculatorBottomSheet(
        stateProvider = state::value,
        decimalSeparator = sheetData.decimalSeparator,
        calculatorActions = sheetData.actions,
        numericKeyboardActions = sheetData.numericKeyboardActions,
      )
    }

    is GeneralBottomSheetData -> GeneralBottomSheet(sheetData)
  }
}

@ExpePreview
@Composable
private fun CreateTransactionScreenPreview(
  @PreviewParameter(CreateTransactionStatePreviewProvider::class) state: CreateTransactionUiState,
) {
  ExpensesTrackerTheme {
    CreateTransactionContent(
      stateProvider = { state },
      bottomSheetStateProvider = { TODO() },
      onCategoryClick = {},
      onBackPressed = {},
      commandProcessor = {},
    )
  }
}