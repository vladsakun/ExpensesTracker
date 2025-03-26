package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.accounts.api.SelectAccountResult
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.AccountIcons
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.DisabledTextColor
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.ui.bottomsheet.BottomScreenTransition
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ScreenWithModalBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.TransactionCalculatorBottomSheet
import com.emendo.expensestracker.core.ui.handleValueResult
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient
import de.palm.composestateevents.EventEffect
import de.palm.composestateevents.triggered
import kotlinx.collections.immutable.ImmutableList
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
  accountResultRecipient: OpenResultRecipient<SelectAccountResult>,
  viewModel: CreateTransactionViewModel = hiltViewModel(),
) {
  accountResultRecipient.handleValueResult(viewModel::updateSelectedAccount)

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
      onCategoryClick = remember { { navigator.navigate(SelectCategoryScreenDestination) } },
      onBackPressed = remember { { navigator.navigateUp() } },
      commandProcessor = remember { viewModel::proceedCommand },
      onCreateAccountClick = remember { { navigator.navigate(viewModel.getCreateAccountScreenRoute()) } },
      onSelectAccountClick = { isSource -> navigator.navigate(viewModel.getSelectAccountScreenRoute(isSource)) },
      onDuplicateClick = remember {
        {
          navigator.navigate(viewModel.getDuplicateTransactionScreenRoute()) {
            // Todo pop screen to align navigateUp behavior
          }
        }
      },
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
  onSelectAccountClick: (Boolean) -> Unit,
  onCreateAccountClick: () -> Unit,
  onDuplicateClick: () -> Unit,
) {
  val scaffoldState = rememberBottomSheetScaffoldState()

  BottomSheetEffects(bottomSheetStateProvider, commandProcessor, scaffoldState)

  BottomSheetScaffold(
    scaffoldState = scaffoldState,
    topBar = { TopBar(stateProvider, commandProcessor, onBackPressed) },
    sheetContent = {
      val bottomSheetData = bottomSheetStateProvider().bottomSheetData
      if (bottomSheetData != null) {
        BottomSheetContent(bottomSheetData)
      }
    },
    // Workaround for issue https://issuetracker.google.com/issues/265444789
    sheetPeekHeight = 0.dp,
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(paddingValues),
    ) {
      val state = stateProvider()

      Header(
        state = state,
        commandProcessor = commandProcessor,
        onCreateAccountClick = onCreateAccountClick,
        onSelectAccountClick = onSelectAccountClick,
      )
      // Todo Extract to a common TransferBlock
      if (state.screenData.transactionType != TransactionType.TRANSFER) {
        CategorySelection(state.target, onCategoryClick)

        // Todo fix Account recomposition on state change
        if (state.accounts.isNotEmpty()) {
          TransactionElementRow(
            label = stringResource(id = R.string.account),
            transactionItem = state.source,
            onClick = { onSelectAccountClick(true) },
          )
          // TODO try movableContentOf
        } else {
          TransactionElementRow(
            label = stringResource(id = R.string.account),
            onClick = onCreateAccountClick,
          ) {
            CreateAccountButton(
              onClick = onCreateAccountClick
            )
          }
        }
      }
      // Todo think about commandProcessor passing
      NoteTextField(
        text = state.note,
        onNoteValueChange = { note -> commandProcessor(UpdateNoteTextCommand(note)) },
        onFocused = { commandProcessor(HideCalculatorBottomSheetCommand()) },
      )
      ExpeDivider()
      SaveButton { commandProcessor(SaveTransactionCommand()) }
      AdditionalActions(commandProcessor, onDuplicateClick)
    }
  }
}

@Composable
private fun AccountsSection(
  accounts: ImmutableList<AccountUiModel>,
  onClick: (AccountUiModel) -> Unit,
) {
  LazyHorizontalStaggeredGrid(
    rows = StaggeredGridCells.Fixed(2),
    horizontalItemSpacing = Dimens.margin_small_x,
    verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
    contentPadding = PaddingValues(horizontal = marginHorizontal, vertical = marginVertical),
    modifier = Modifier
      .fillMaxWidth()
      .height(128.dp),
  ) {
    items(accounts) { account ->
      AccountChip(
        accountUiModel = account,
        onClick = { onClick(account) },
      )
    }
  }

  ExpeDivider()
}

@Composable
private fun AccountChip(accountUiModel: AccountUiModel, onClick: () -> Unit) {
  FilterChip(
    modifier = Modifier.heightIn(min = Dimens.icon_button_size),
    onClick = onClick,
    label = { Text(accountUiModel.name.stringValue()) },
    selected = accountUiModel.selected,
    leadingIcon = {
      Icon(
        imageVector = accountUiModel.icon.imageVector,
        contentDescription = null,
        modifier = Modifier.size(FilterChipDefaults.IconSize),
      )
    }
  )
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
  noinline onCreateAccountClick: () -> Unit,
  noinline onSelectAccountClick: (Boolean) -> Unit,
) {
  // Todo remove Uncategorized visible during animation when switching transaction type
  AnimatedContent(
    targetState = state.screenData.transactionType == TransactionType.TRANSFER,
    label = "header",
  ) { isTransfer ->
    if (isTransfer) {
      TransferBlock(
        state = state,
        commandProcessor = commandProcessor,
        onCreateAccountClick = onCreateAccountClick,
        onSelectAccountClick = onSelectAccountClick,
      )
    } else {
      IncomeExpenseBlock(state, commandProcessor)
    }
  }

  ExpeDivider()
}

@Composable
private inline fun IncomeExpenseBlock(
  state: CreateTransactionUiState,
  crossinline commandProcessor: @DisallowComposableCalls (CreateTransactionCommand) -> Unit,
) {
  val focusManager = LocalFocusManager.current

  Column(
    modifier = Modifier
      .clickable {
        commandProcessor(ShowCalculatorBottomSheetCommand(true))
        focusManager.clearFocus(force = true)
      },
  ) {
    Amount(
      text = state.amount.formattedValue,
      transactionType = state.screenData.transactionType,
      error = state.screenData.amountError == triggered,
      onErrorConsumed = remember { { commandProcessor(ConsumeFieldErrorCommand(FieldWithError.Amount)) } },
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
  noinline onCreateAccountClick: () -> Unit,
  noinline onSelectAccountClick: (Boolean) -> Unit,
) {
  TransferRow {
    TransferSource(
      state = state,
      commandProcessor = commandProcessor,
      onCreateAccountClick = onCreateAccountClick,
      onSelectAccountClick = { onSelectAccountClick(true) },
    )
    Chevron(
      width = CHEVRON_WIDTH.dp,
      height = TRANSFER_BLOCK_MIN_HEIGHT.dp,
    )
    TransferTarget(
      state = state,
      commandProcessor = commandProcessor,
      onCreateAccountClick = onCreateAccountClick,
      onSelectAccountClick = { onSelectAccountClick(false) },
    )
  }
}

@Composable
private inline fun RowScope.TransferSource(
  state: CreateTransactionUiState,
  crossinline commandProcessor: (CreateTransactionCommand) -> Unit,
  noinline onCreateAccountClick: () -> Unit,
  noinline onSelectAccountClick: () -> Unit,
) {
  TransferEntity(
    isSource = true,
    transactionItemModel = state.source,
    accounts = state.accounts,
    commandProcessor = commandProcessor,
    onSelectAccountClick = onSelectAccountClick,
    onCreateAccountClick = onCreateAccountClick,
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
  noinline onCreateAccountClick: () -> Unit,
  noinline onSelectAccountClick: () -> Unit,
) {
  TransferEntity(
    isSource = false,
    transactionItemModel = state.target,
    accounts = state.accounts,
    commandProcessor = commandProcessor,
    onCreateAccountClick = onCreateAccountClick,
    onSelectAccountClick = onSelectAccountClick,
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
  isSource: Boolean,
  transactionItemModel: TransactionItemModel?,
  accounts: ImmutableList<AccountUiModel>,
  crossinline commandProcessor: (CreateTransactionCommand) -> Unit,
  noinline onCreateAccountClick: () -> Unit,
  noinline onSelectAccountClick: () -> Unit,
  crossinline amountBlock: @Composable() (ColumnScope.() -> Unit),
) {
  TransferColumn {
    // Todo will be removed
    if (accounts.size <= 1) {
      CreateAccountButton(onCreateAccountClick)
      return
    }

    TransferAccount(
      onSelectAccountClick = onSelectAccountClick,
      accountModel = transactionItemModel,
      accounts = accounts,
    )
    Column(
      modifier = Modifier.clickable(onClick = { commandProcessor(ShowCalculatorBottomSheetCommand(isSource)) }),
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
    DisabledTextColor
  }

@Composable
private fun AdditionalActions(
  commandProcessor: (CreateTransactionCommand) -> Unit,
  onDuplicateClick: () -> Unit,
) {
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
        onClick = onDuplicateClick,
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
    colors = ButtonDefaults.filledTonalButtonColors(),
    textStyle = MaterialTheme.typography.labelSmall,
    fillWidth = false,
  )
}

@Composable
@Suppress("NOTHING_TO_INLINE") // inlined to avoid unnecessary recomposition
private inline fun TransferAccount(
  accountModel: TransactionItemModel?,
  accounts: ImmutableList<AccountUiModel>,
  noinline onSelectAccountClick: () -> Unit,
) {
  //  var expanded by remember { mutableStateOf(false) }
  //  val animateRotation = animateFloatAsState(if (expanded) 1.0f else 0.0f)
  //  val scrollState = rememberScrollState()
  val enabled = remember(accountModel) { accountModel != null }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.TopStart)
      .clickable(onClick = onSelectAccountClick),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
      modifier = Modifier.fillMaxWidth()
    ) {
      Icon(
        modifier = Modifier.size(Dimens.icon_size),
        imageVector = accountModel?.icon?.imageVector ?: AccountIcons.Wallet,
        contentDescription = null,
        tint = if (enabled) LocalContentColor.current else DisabledTextColor,
      )
      Text(
        text = accountModel?.name?.stringValue() ?: stringResource(R.string.create_transaction_general_destination),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.weight(1f),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        color = if (enabled) Color.Unspecified else DisabledTextColor,
      )
      Icon(
        imageVector = Icons.Default.ArrowDropDown,
        contentDescription = null,
        modifier = Modifier.align(Alignment.CenterVertically),
      )
    }

    // Todo make dropdown menu width as wide of Row
    //    DropdownMenu(
    //      expanded = expanded,
    //      onDismissRequest = {
    //        expanded = false
    //      },
    //      properties = PopupProperties(
    //        focusable = true,
    //        dismissOnBackPress = true,
    //        dismissOnClickOutside = true,
    //      ),
    //      scrollState = scrollState,
    //      offset = DpOffset(0.dp, Dimens.margin_small_x),
    //      modifier = Modifier.fillMaxWidth(.4f),
    //    ) {
    //      accounts
    //        .filter { it.id != accountModel?.id }
    //        .forEach { account: AccountUiModel ->
    //          DropdownMenuItem(
    //            text = { Text(account.name.stringValue()) },
    //            onClick = {
    //              onSelectAccountClick()
    //              expanded = false
    //            },
    //            leadingIcon = {
    //              Icon(
    //                imageVector = account.icon.imageVector,
    //                contentDescription = null,
    //              )
    //            }
    //          )
    //        }
    //    }
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
      bottomSheetStateProvider = { CreateTransactionBottomSheetState() },
      onCategoryClick = {},
      onBackPressed = {},
      commandProcessor = {},
      onCreateAccountClick = {},
      onSelectAccountClick = { _ -> },
      onDuplicateClick = {},
    )
  }
}