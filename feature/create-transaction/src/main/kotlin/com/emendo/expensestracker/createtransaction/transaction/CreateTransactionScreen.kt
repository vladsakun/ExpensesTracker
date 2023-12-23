package com.emendo.expensestracker.createtransaction.transaction

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.app.resources.models.ColorModel.Companion.color
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import com.emendo.expensestracker.core.data.model.transaction.TransactionType.Companion.toTransactionType
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.theme.PlaceholderTextStyle
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.ui.bottomsheet.BottomScreenTransition
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ScreenWithModalBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.TransactionCalculatorBottomSheet
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.createtransaction.destinations.SelectCategoryScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import de.palm.composestateevents.EventEffect
import de.palm.composestateevents.NavigationEventEffect
import de.palm.composestateevents.triggered
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

private val marginVertical = Dimens.margin_large_x
private val marginHorizontal = Dimens.margin_large_x
private const val ERROR_ANIMATION_DURATION_MILLIS = 500
private const val TRANSFER_BLOCK_MIN_HEIGHT = 100

@RootNavGraph(start = true)
@Destination(style = BottomScreenTransition::class)
@Composable
fun CreateTransactionScreen(
  navigator: DestinationsNavigator,
  viewModel: CreateTransactionViewModel = hiltViewModel(),
) {
  val uiState = viewModel.uiState.collectAsStateWithLifecycle()
  val bottomSheetState: State<CreateTransactionBottomSheetState> =
    viewModel.bottomSheetState.collectAsStateWithLifecycle()
  val calculatorTextState: State<String> = viewModel.calculatorText.collectAsStateWithLifecycle()

  ScreenWithModalBottomSheet(
    stateManager = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    bottomSheetContent = { type -> BottomSheetContent(type) },
  ) {
    CreateTransactionContent(
      stateProvider = uiState::value,
      bottomSheetStateProvider = bottomSheetState::value,
      calculatorTextStateProvider = calculatorTextState::value,
      onSourceAmountClick = remember { { viewModel.showCalculatorBottomSheet() } },
      onTargetAmountClick = remember { { viewModel.showCalculatorBottomSheet(sourceTrigger = false) } },
      onCategoryClick = remember { { navigator.navigate(SelectCategoryScreenDestination) } },
      onAccountClick = remember { viewModel::openAccountListScreen },
      onTransferTargetAccountClick = remember { viewModel::selectTransferTargetAccount },
      onCreateTransactionClick = remember { viewModel::saveTransaction },
      onBackPressed = remember { { navigator.navigateUp() } },
      onErrorConsumed = remember { viewModel::consumeFieldError },
      onConsumedNavigateUpEvent = remember { viewModel::consumeCloseEvent },
      onConsumedShowCalculatorBottomSheetEvent = remember { viewModel::consumeShowCalculatorBottomSheet },
      onConsumedHideCalculatorBottomSheetEvent = remember { viewModel::consumeHideCalculatorBottomSheet },
      onTransactionTypeChange = remember { viewModel::changeTransactionType },
      onNoteValueChange = remember { viewModel::updateNoteText },
      onDeleteClick = remember { viewModel::showConfirmDeleteTransactionBottomSheet },
      onDuplicateClick = remember { viewModel::duplicateTransaction },
      hideCalculatorBottomSheet = remember { viewModel::hideCalculatorBottomSheet },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTransactionContent(
  stateProvider: () -> CreateTransactionUiState,
  bottomSheetStateProvider: () -> CreateTransactionBottomSheetState,
  calculatorTextStateProvider: () -> String,
  onSourceAmountClick: () -> Unit,
  onTargetAmountClick: () -> Unit,
  onCategoryClick: () -> Unit,
  onAccountClick: () -> Unit,
  onTransferTargetAccountClick: () -> Unit,
  onCreateTransactionClick: () -> Unit,
  onBackPressed: () -> Unit,
  onErrorConsumed: (field: FieldWithError) -> Unit,
  onConsumedNavigateUpEvent: () -> Unit,
  onConsumedShowCalculatorBottomSheetEvent: () -> Unit,
  onConsumedHideCalculatorBottomSheetEvent: () -> Unit,
  onTransactionTypeChange: (TransactionType) -> Unit,
  onNoteValueChange: (String) -> Unit,
  onDeleteClick: () -> Unit,
  onDuplicateClick: () -> Unit,
  hideCalculatorBottomSheet: () -> Unit,
) {
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberBottomSheetScaffoldState()
  val focusManager = LocalFocusManager.current

  EventEffect(
    event = bottomSheetStateProvider().hide,
    onConsumed = onConsumedHideCalculatorBottomSheetEvent,
    action = {
      scope.launch {
        scaffoldState.bottomSheetState.partialExpand()
      }
    },
  )

  EventEffect(
    event = bottomSheetStateProvider().show,
    onConsumed = onConsumedShowCalculatorBottomSheetEvent,
    action = {
      scope.launch {
        scaffoldState.bottomSheetState.expand()
      }
    },
  )

  val state = stateProvider()

  // Todo extract navigate up to a separate flow
  NavigationEventEffect(
    event = state.screenData.navigateUp,
    onConsumed = onConsumedNavigateUpEvent,
    action = onBackPressed,
  )

  BottomSheetScaffold(
    scaffoldState = scaffoldState,
    sheetContent = {
      val bottomSheetData = bottomSheetStateProvider().data
      if (bottomSheetData != null) {
        BottomSheetContent(sheetData = bottomSheetData)
      }
    },
    // Workaround for issue https://issuetracker.google.com/issues/265444789
    sheetPeekHeight = 0.dp,
    topBar = {
      ExpeCenterAlignedTopBar(
        title = {
          val tabsResId = persistentListOf(R.string.income, R.string.expense, R.string.transfer)
          // Todo improve transaction type switch animation
          TextSwitch(
            selectedIndex = state.screenData.transactionType.ordinal,
            items = tabsResId.map { stringResource(id = it) }.toPersistentList(),
            onSelectionChange = { tabIndex ->
              onTransactionTypeChange(tabIndex.toTransactionType())
            },
          )
        },
        navigationIcon = { NavigationBackIcon(onNavigationClick = onBackPressed) },
        actions = persistentListOf(
          MenuAction(
            icon = ExpeIcons.Check,
            onClick = onCreateTransactionClick,
            contentDescription = stringResource(id = R.string.confirm),
          )
        ),
      )
    },
    modifier = Modifier
      .consumeWindowInsets(WindowInsets(0, 0, 0, 0))
      .fillMaxSize(),
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(paddingValues)
    ) {
      if (state.screenData.transactionType == TransactionType.TRANSFER) {
        TransferRow {
          TransferColumn {
            val source = state.source
            if (source == null) {
              CreateAccountButton(onAccountClick)
            } else {
              TransferAccount(onAccountClick, source)
              Column(modifier = Modifier.clickable(onClick = onSourceAmountClick)) {
                TransferAmount(text = state.amount.formattedValue)
                EditableAmount(
                  textProvider = calculatorTextStateProvider(),
                  focused = state.sourceAmountFocused,
                )
              }
            }
          }
          Chevron()
          TransferColumn {
            val target = state.target
            if (target == null) {
              CreateAccountButton(onAccountClick)
            } else {
              TransferAccount(onTransferTargetAccountClick, target)
              Column(modifier = Modifier.clickable(onClick = onTargetAmountClick)) {
                state.transferReceivedAmount?.let { amount ->
                  TransferAmount(
                    text = amount.formattedValue,
                    textColor = if (state.isCustomTransferAmount) MaterialTheme.customColorsPalette.successColor else MaterialTheme.colorScheme.outline,
                  )
                  EditableAmount(
                    textProvider = "test",
                    focused = state.transferTargetAmountFocused,
                  )
                }
              }
            }
          }
        }
      } else {
        Column(
          modifier = Modifier
            .clickable {
              onSourceAmountClick()
              focusManager.clearFocus(force = true)
            },
        ) {
          Amount(
            text = state.amount.formattedValue,
            transactionType = state.screenData.transactionType,
            error = state.screenData.amountError == triggered,
            onErrorConsumed = { onErrorConsumed(FieldWithError.Amount) },
          )
          EditableAmount(
            textProvider = calculatorTextStateProvider(),
            focused = state.sourceAmountFocused,
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = marginHorizontal, vertical = Dimens.margin_small_xx),
          )
        }
      }
      ExpeDivider()
      if (state.screenData.transactionType != TransactionType.TRANSFER) {
        TransactionElementRow(
          transactionItem = state.target,
          label = stringResource(id = R.string.category),
          onClick = onCategoryClick,
        )
        TransactionElementRow(
          transactionItem = state.source,
          label = stringResource(id = R.string.account),
          onClick = onAccountClick,
          error = state.screenData.sourceError == triggered,
          onErrorConsumed = { onErrorConsumed(FieldWithError.Source) },
        )
        ExpeDivider()
      }
      NoteTextField(
        text = state.note,
        onNoteValueChange = onNoteValueChange,
        onFocused = {
          hideCalculatorBottomSheet()
        },
      )
      ExpeDivider()
      VerticalSpacer(marginVertical)
      SaveButton(onCreateTransactionClick)
      VerticalSpacer(marginVertical)
      Column {
        CreateTransactionRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(marginVertical),
        ) {
          AdditionalAction(
            titleResId = R.string.delete,
            icon = ExpeIcons.Delete,
            onClick = onDeleteClick,
          )
          AdditionalAction(
            titleResId = R.string.duplicate,
            icon = ExpeIcons.FileCopy,
            onClick = onDuplicateClick,
          )
        }
      }
    }
  }
}

@Composable
private fun EditableAmount(
  textProvider: String,
  focused: Boolean,
  modifier: Modifier = Modifier,
) {
  AutoSizableTextField(
    text = textProvider,
    minFontSize = MaterialTheme.typography.labelSmall.fontSize,
    textAlign = TextAlign.End,
    style = MaterialTheme.typography.labelSmall,
    maxLines = 1,
    focused = focused,
    modifier = modifier,
  )
}

@Composable
private fun SaveButton(onClick: () -> Unit) {
  ExpeButton(
    textResId = R.string.save_transaction,
    onClick = onClick,
    modifier = Modifier.padding(horizontal = marginHorizontal)
  )
}

// Todo hide calculator bottom sheet on note focus
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
private fun RowScope.TransferColumn(content: @Composable ColumnScope.() -> Unit) {
  Column(
    modifier = Modifier.weight(1f),
    verticalArrangement = Arrangement.Center,
    content = content,
  )
}

@Composable
private fun TransferRow(content: @Composable RowScope.() -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = TRANSFER_BLOCK_MIN_HEIGHT.dp)
      .padding(horizontal = marginHorizontal, vertical = marginVertical),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(marginVertical),
    content = content,
  )
}

@Composable
private fun Chevron() {
  Chevron(
    modifier = Modifier
      .width(12.dp)
      .height(TRANSFER_BLOCK_MIN_HEIGHT.dp)
  )
}

@Composable
private fun TransferAmount(
  text: String,
  modifier: Modifier = Modifier,
  textColor: Color = MaterialTheme.typography.headlineMedium.color,
) {
  AutoSizableText(
    text = text,
    modifier = modifier,
    minFontSize = MaterialTheme.typography.bodyMedium.fontSize,
    style = MaterialTheme.typography.headlineMedium,
    color = textColor,
    textAlign = TextAlign.End,
    maxLines = 1,
  )
}

@Composable
private fun TransferAccount(
  onClick: () -> Unit,
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
private fun RowScope.AdditionalAction(
  @StringRes titleResId: Int,
  icon: ImageVector,
  onClick: () -> Unit,
  enabled: Boolean = true,
) {
  ExpeButtonWithIcon(
    titleResId = titleResId,
    icon = icon,
    onClick = onClick,
    enabled = enabled,
    modifier = Modifier
      .weight(1f)
      .heightIn(min = Dimens.icon_button_size),
  )
}

@Composable
private fun Amount(
  text: String,
  transactionType: TransactionType,
  error: Boolean,
  onErrorConsumed: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val backgroundColor = animateColorAsState(
    targetValue = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
    finishedListener = { onErrorConsumed() },
    animationSpec = tween(ERROR_ANIMATION_DURATION_MILLIS),
    label = "error"
  )
  AutoSizableText(
    text = text,
    minFontSize = MaterialTheme.typography.bodyMedium.fontSize,
    color = transactionType.amountColor(),
    style = MaterialTheme.typography.headlineMedium,
    textAlign = TextAlign.End,
    maxLines = 1,
    modifier = modifier
      .fillMaxWidth()
      .drawBehind { drawRect(backgroundColor.value) }
      .padding(horizontal = marginHorizontal),
  )
}

@Composable
private fun ColumnScope.BottomSheetContent(sheetData: BottomSheetData) {
  // Todo rethink ModalBottomSheet. It would be nice to switch transaction type (to Transfer) without closing the keyboard
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

@Composable
private fun TransactionElementRow(
  transactionItem: TransactionItemModel?,
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  error: Boolean = false,
  onErrorConsumed: () -> Unit = {},
) {
  val backgroundColor = animateColorAsState(
    targetValue = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
    finishedListener = { onErrorConsumed() },
    animationSpec = tween(ERROR_ANIMATION_DURATION_MILLIS),
    label = "error"
  )
  CreateTransactionRow(
    modifier = modifier
      .clickable(onClick = onClick)
      .drawBehind { drawRect(backgroundColor.value) },
  ) {
    Text(
      text = label,
      style = PlaceholderTextStyle,
    )
    Spacer(modifier = Modifier.width(Dimens.margin_small_x))
    Spacer(modifier = Modifier.weight(1f))
    transactionItem?.let { model ->
      TransactionElement(
        icon = model.icon.imageVector,
        title = model.name.stringValue(),
        tint = model.color.color,
      )
    }
  }
  ExpeDivider()
}

@Composable
private fun TransactionElement(
  icon: ImageVector,
  title: String,
  tint: Color,
) {
  Icon(
    imageVector = icon,
    contentDescription = null,
    tint = tint,
  )
  Spacer(modifier = Modifier.width(Dimens.margin_small_xx))
  Text(
    text = title,
    textAlign = TextAlign.End,
    overflow = TextOverflow.Ellipsis,
    maxLines = 1,
  )
}

@Composable
private fun CreateTransactionRow(
  modifier: Modifier = Modifier,
  horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
  content: @Composable RowScope.() -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = horizontalArrangement,
    modifier = modifier.padding(vertical = marginVertical, horizontal = marginHorizontal),
    content = content,
  )
}

@Composable
@ReadOnlyComposable
fun TransactionType.amountColor(): Color {
  if (this == TransactionType.INCOME) {
    return MaterialTheme.customColorsPalette.successColor
  }

  return LocalTextStyle.current.color
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
      calculatorTextStateProvider = { TODO() },
      onSourceAmountClick = {},
      onTargetAmountClick = {},
      onCategoryClick = {},
      onAccountClick = {},
      onTransferTargetAccountClick = {},
      onCreateTransactionClick = {},
      onBackPressed = {},
      onErrorConsumed = { _ -> },
      onConsumedNavigateUpEvent = {},
      onConsumedShowCalculatorBottomSheetEvent = {},
      onConsumedHideCalculatorBottomSheetEvent = {},
      onTransactionTypeChange = { _ -> },
      onNoteValueChange = { _ -> },
      onDeleteClick = {},
      onDuplicateClick = {},
      hideCalculatorBottomSheet = {},
    )
  }
}