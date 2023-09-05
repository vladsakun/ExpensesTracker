package com.emendo.accounts.create

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.accounts.create.bottomsheet.ColorsBottomSheet
import com.emendo.accounts.create.bottomsheet.CurrenciesBottomSheet
import com.emendo.accounts.create.bottomsheet.IconsBottomSheet
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.component.bottomsheet.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.utils.*
import com.emendo.expensestracker.core.ui.*
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetType
import com.emendo.expensestracker.core.ui.bottomsheet.calculator.InitialBalanceBS
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

const val SELECTED_ICON_BORDER_WIDTH = 3
const val SELECTED_COLOR_BORDER_WIDTH = 3
const val SELECTED_ITEM_ALPHA_BORDER = 0.2f
const val ITEM_FIXED_SIZE_DP = 60

@Destination
@Composable
fun CreateAccountRoute(
  navigator: DestinationsNavigator,
  viewModel: CreateAccountViewModel = hiltViewModel(),
) {
  // Todo ask Pavel Haluza why Remember(viewModel) does not work
  val onAccountNameChange = remember { { model: String -> viewModel.setAccountName(model) } }
  val onDismissRequest = remember { { viewModel.onDismissRequest() } }
  CreateAccountScreen(
    state = viewModel.state.collectAsStateWithLifecycle(),
    bottomSheetType = viewModel.bottomSheet.collectAsStateWithLifecycle(),
    navigateUpEvent = viewModel.navigateUpEvent,
    hideBottomSheetEvent = viewModel.hideBottomSheetEvent,
    onAccountNameChange = onAccountNameChange,
    onCreateAccountClick = viewModel::createNewAccount,
    onNavigationClick = navigator::navigateUp,
    onInitialBalanceRowClick = viewModel::onInitialBalanceClick,
    onDismissRequest = onDismissRequest,
    onIconRowClick = viewModel::onIconRowClick,
    onColorRowClick = viewModel::onColorRowClick,
    onCurrencyRowClick = viewModel::onCurrencyRowClick,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateAccountScreen(
  state: State<CreateAccountScreenData>,
  bottomSheetType: State<BottomSheetType?>,
  hideBottomSheetEvent: Flow<Unit>,
  navigateUpEvent: Flow<Unit>,
  onAccountNameChange: (model: String) -> Unit,
  onCreateAccountClick: () -> Unit,
  onNavigationClick: () -> Unit,
  onInitialBalanceRowClick: () -> Unit,
  onDismissRequest: () -> Unit,
  onIconRowClick: () -> Unit,
  onColorRowClick: () -> Unit,
  onCurrencyRowClick: () -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val closeBottomSheet: () -> Unit = remember(bottomSheetState) {
    {
      coroutineScope.launch {
        bottomSheetState.hide()
      }
    }
  }

  BackHandler {
    when {
      bottomSheetState.isVisible -> closeBottomSheet()
      else -> onNavigationClick()
    }
  }

  LaunchedEffect(Unit) { hideBottomSheetEvent.collect { closeBottomSheet() } }
  LaunchedEffect(Unit) { navigateUpEvent.collect { onNavigationClick() } }
  LaunchedEffect(Unit) {
    snapshotFlow { bottomSheetState.currentValue }
      .collect {
        if (it == SheetValue.Hidden) onDismissRequest()
      }
  }

  val TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  val isCreateButtonEnabled = remember { derivedStateOf { state.value.isCreateAccountButtonEnabled } }
  val scrollState = rememberScrollState()

  ExpeScaffold(
    modifier = Modifier
      .fillMaxSize()
      .nestedScroll(TopAppBarScrollBehavior.nestedScrollConnection),
    topBar = {
      ExpeTopBar(
        titleRes = R.string.create_account,
        navigationIcon = { NavigationBackIcon(onNavigationClick = onNavigationClick) },
        scrollBehavior = TopAppBarScrollBehavior,
      )
    },
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .verticalScroll(scrollState)
        .padding(paddingValues)
        .padding(Dimens.margin_large_x),
      verticalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
    ) {
      AccountName(
        accountName = state.value.accountName,
        onAccountNameChange = onAccountNameChange,
      )
      IconRow(
        icon = state.value.icon,
        onClick = onIconRowClick,
      )
      ColorRow(
        color = state.value.color,
        onClick = onColorRowClick,
      )
      // Todo ask Anton
      Spacer(modifier = Modifier.height(Dimens.margin_large_x))
      InitialBalanceRow(
        initialBalanceState = state.value.initialBalance,
        onInitialBalanceRowClick = onInitialBalanceRowClick,
      )
      CurrencyRow(
        selectedCurrency = state.value.currency,
        onClick = onCurrencyRowClick,
      )
      ExpeButton(
        textResId = R.string.create,
        onClick = onCreateAccountClick,
        enabled = isCreateButtonEnabled.value,
      )
    }
  }

  CreateAccountModalBottomSheet(
    bottomSheetState = bottomSheetState,
    bottomSheetType = bottomSheetType,
    closeBottomSheet = closeBottomSheet,
  ) {
    BottomSheetContent(bottomSheetType.value, closeBottomSheet)
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CreateAccountModalBottomSheet(
  bottomSheetState: SheetState,
  bottomSheetType: State<BottomSheetType?>,
  closeBottomSheet: () -> Unit,
  bottomSheetContent: @Composable ColumnScope.() -> Unit,
) {
  val shouldOpenBottomSheet = remember { derivedStateOf { bottomSheetType.value != null } }
  val focusManager = LocalFocusManager.current

  if (shouldOpenBottomSheet.value) {
    focusManager.clearFocus()
    ModalBottomSheet(
      onDismissRequest = {},
      sheetState = bottomSheetState,
      windowInsets = WindowInsets(0),
      shape = ExpeBottomSheetShape,
      content = bottomSheetContent,
    )
  }
}

@Composable
private fun AccountName(
  accountName: String,
  onAccountNameChange: (name: String) -> Unit,
) {
  ExpeTextField(
    label = stringResource(id = R.string.account_name),
    text = accountName,
    onValueChange = onAccountNameChange,
  )
}

@Composable
private fun IconRow(
  icon: IconModel,
  onClick: () -> Unit,
) {
  SelectRowWithIcon(
    labelResId = R.string.icon,
    imageVector = icon.imageVector,
    onClick = onClick,
  )
}

@Composable
private fun ColorRow(
  color: ColorModel,
  onClick: () -> Unit,
) {
  SelectRowWithColor(
    labelResId = R.string.color,
    color = color.color,
    onClick = onClick,
  )
}

@Composable
private fun CurrencyRow(
  selectedCurrency: CurrencyModel,
  onClick: () -> Unit,
) {
  SelectRowWithText(
    labelResId = R.string.currency,
    text = selectedCurrency.currencySymbol,
    textStyle = MaterialTheme.typography.bodyLarge,
    onClick = onClick,
  )
}

@Composable
private fun InitialBalanceRow(
  initialBalanceState: String,
  onInitialBalanceRowClick: () -> Unit,
) {
  SelectRowWithText(
    labelResId = R.string.balance,
    text = initialBalanceState,
    textStyle = MaterialTheme.typography.bodyLarge,
    onClick = onInitialBalanceRowClick,
  )
}

@Composable
private fun BottomSheetContent(
  type: BottomSheetType?,
  hideBottomSheet: () -> Unit,
) {
  when (type) {
    is BottomSheetType.Currency -> {
      CurrenciesBottomSheet(
        currencies = CurrencyModel.entries.toImmutableList(),
        onSelectCurrency = {
          type.onSelectCurrency(it)
          hideBottomSheet()
        },
        onCloseClick = hideBottomSheet,
      )
    }

    is BottomSheetType.Color -> {
      ColorsBottomSheet(
        colors = ColorModel.entries.toImmutableList(),
        selectedColor = type.selectedColor,
        onColorSelect = {
          type.onSelectColor(it)
          hideBottomSheet()
        },
        onCloseClick = hideBottomSheet,
      )
    }

    is BottomSheetType.Icon -> {
      IconsBottomSheet(
        icons = IconModel.entries.toImmutableList(),
        onIconSelect = {
          type.onSelectIcon(it)
          hideBottomSheet()
        },
        onCloseClick = hideBottomSheet,
        selectedIcon = type.selectedIcon,
      )
    }

    is BottomSheetType.Calculator -> {
      val text = type.text.collectAsStateWithLifecycle()
      val equalButtonState = type.equalButtonState.collectAsStateWithLifecycle()
      InitialBalanceBS(
        text = text,
        initialBalanceActions = type.initialBalanceActions,
        equalButtonState = equalButtonState,
        decimalSeparator = type.decimalSeparator,
        currency = type.currency,
      )
    }

    else -> Unit
  }
}

@ExpePreview
@Composable
private fun CreateAccountScreenPreview(
  @PreviewParameter(CreateAccountPreviewData::class) previewData: CreateAccountScreenData,
) {
  val state = remember { mutableStateOf(previewData) }
  val bottomSheetState = remember { mutableStateOf(null) }

  ExpensesTrackerTheme {
    CreateAccountScreen(
      state = state,
      bottomSheetType = bottomSheetState,
      hideBottomSheetEvent = emptyFlow(),
      navigateUpEvent = emptyFlow(),
      onAccountNameChange = {},
      onCreateAccountClick = {},
      onNavigationClick = {},
      onInitialBalanceRowClick = {},
      onDismissRequest = {},
      onIconRowClick = {},
      onColorRowClick = {},
      onCurrencyRowClick = {},
    )
  }
}