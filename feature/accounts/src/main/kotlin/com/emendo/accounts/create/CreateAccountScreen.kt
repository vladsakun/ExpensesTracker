package com.emendo.accounts.create

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_CREATE_ACCOUNT_BALANCE_BOTTOM_SHEET
import com.emendo.expensestracker.core.data.model.AccountIconModel
import com.emendo.expensestracker.core.data.model.ColorModel
import com.emendo.expensestracker.core.data.model.CurrencyModel
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.component.bottomsheet.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.utils.*
import com.emendo.expensestracker.feature.accounts.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.emendo.expensestracker.core.app.resources.R as AppR

private const val SELECTED_ICON_BORDER_WIDTH = 3
private const val SELECTED_COLOR_BORDER_WIDTH = 3
private const val SELECTED_ITEM_ALPHA_BORDER = 0.2f
private const val ITEM_FIXED_SIZE_DP = 60

@Destination
@Composable
fun CreateAccountRoute(
  navigator: DestinationsNavigator,
  viewModel: CreateAccountViewModel = hiltViewModel(),
) {
  CreateAccountScreen(
    state = viewModel.state.collectAsStateWithLifecycle(),
    onNavigationClick = navigator::navigateUp,
    onAccountNameChange = viewModel::setAccountName,
    onIconClick = viewModel::setIcon,
    onColorClick = viewModel::setColor,
    onCurrencyClick = viewModel::setCurrency,
    onCreateAccountClick = {
      navigator.navigateUp()
      viewModel.createNewAccount()
    },
    initialBalanceActions = NumKeyboardActions.InitialBalanceActions(
      onChangeSignClick = viewModel::onChangeSignClick,
      onCurrencyClick = viewModel::onCurrencyClick,
      onClearClick = viewModel::onClearClick,
      onMathOperationClick = viewModel::onMathOperationClick,
      onNumberClick = viewModel::onNumberClick,
      onPrecisionClick = viewModel::onPrecisionClick,
      onDoneClick = viewModel::onConfirmClick,
      onEqualClick = viewModel::onEqualClick,
    )
  )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun CreateAccountScreen(
  state: State<CreateAccountScreenData>,
  onAccountNameChange: (model: String) -> Unit,
  onIconClick: (model: AccountIconModel) -> Unit,
  onColorClick: (model: ColorModel) -> Unit,
  onCurrencyClick: (model: CurrencyModel) -> Unit,
  onCreateAccountClick: () -> Unit,
  onNavigationClick: () -> Unit,
  initialBalanceActions: NumKeyboardActions.InitialBalanceActions,
) {
  val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  val coroutineScope = rememberCoroutineScope()
  // Todo try go from one BottomSheet to next
  val currentBottomSheet: MutableState<BottomSheetType> = rememberSaveable { mutableStateOf(BottomSheetType.Initial) }
  val keyboardController = LocalSoftwareKeyboardController.current
  val focusManager = LocalFocusManager.current

  val shouldOpenBottomSheet = rememberSaveable { mutableStateOf(false) }
  val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val closeBottomSheet: () -> Unit = {
    hideKeyboard(keyboardController, focusManager)
    coroutineScope.launch {
      bottomSheetState.hide()
      shouldOpenBottomSheet.value = false
      initialBalanceActions.onDoneClick()
    }
  }

  val openBottomSheet: (BottomSheetType) -> Unit = {
    hideKeyboard(keyboardController, focusManager)
    currentBottomSheet.value = it
    coroutineScope.launch {
      shouldOpenBottomSheet.value = true
    }
  }

  BackHandler {
    when {
      bottomSheetState.isVisible -> closeBottomSheet()
      else -> onNavigationClick()
    }
  }

  ExpeScaffold(
    modifier = Modifier
      .fillMaxSize()
      .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
    topBar = {
      ExpeTopAppBar(
        titleRes = R.string.create_account,
        navigationIcon = { NavigationBackIcon(onNavigationClick = onNavigationClick) },
        scrollBehavior = topAppBarScrollBehavior,
      )
    },
  ) { paddingValues ->
    val accountNameState = remember { derivedStateOf { state.value.accountName } }
    val iconState = remember { derivedStateOf { state.value.icon } }
    val colorState = remember { derivedStateOf { state.value.color } }
    val currencyModelState = remember { derivedStateOf { state.value.currency } }
    val currencyState = remember { derivedStateOf { state.value.currency.currencySymbol } }
    val initialBalanceState = remember { derivedStateOf { state.value.initialBalance } }
    val equalButtonState = remember { derivedStateOf { state.value.equalButtonState } }
    val decimalSeparator = remember { derivedStateOf { state.value.decimalSeparator } }
    val isCreateAccountButtonEnabled = remember { derivedStateOf { state.value.isCreateAccountButtonEnabled } }

    CreateAccountContent(
      paddingValues = paddingValues,
      nameState = accountNameState,
      iconState = iconState,
      colorState = colorState,
      initialBalanceState = initialBalanceState,
      currencyState = currencyState,
      equalButtonState = equalButtonState,
      initialBalanceActions = initialBalanceActions.copy(
        onDoneClick = {
          closeBottomSheet()
          initialBalanceActions.onDoneClick()
        }
      ),
      decimalSeparator = decimalSeparator,
      currencyModelState = currencyModelState,
      onAccountNameChange = onAccountNameChange,
      onIconClick = onIconClick,
      onColorClick = onColorClick,
      onCurrencyClick = onCurrencyClick,
      onCreateAccountClick = onCreateAccountClick,
      openBottomSheet = openBottomSheet,
      isCreateButtonEnabled = isCreateAccountButtonEnabled,
    )
  }

  ModalBottomSheet(
    shouldOpenBottomSheet = shouldOpenBottomSheet,
    bottomSheetState = bottomSheetState,
    currentBottomSheet = currentBottomSheet,
    closeBottomSheet = closeBottomSheet,
    onDismissRequest = { shouldOpenBottomSheet.value = false },
  )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ModalBottomSheet(
  shouldOpenBottomSheet: MutableState<Boolean>,
  bottomSheetState: SheetState,
  currentBottomSheet: MutableState<BottomSheetType>,
  closeBottomSheet: () -> Unit,
  onDismissRequest: () -> Unit,
) {
  if (shouldOpenBottomSheet.value) {
    ModalBottomSheet(
      onDismissRequest = onDismissRequest,
      sheetState = bottomSheetState,
      windowInsets = WindowInsets(0),
      shape = ExpeBottomSheetShape,
    ) {
      BottomSheetContent(currentBottomSheet.value, closeBottomSheet)
    }
  }
}

@Composable
private fun CreateAccountContent(
  paddingValues: PaddingValues,
  nameState: State<String>,
  iconState: State<AccountIconModel>,
  colorState: State<ColorModel>,
  initialBalanceState: State<String>,
  currencyState: State<String>,
  equalButtonState: State<EqualButtonState>,
  initialBalanceActions: NumKeyboardActions.InitialBalanceActions,
  decimalSeparator: State<String>,
  currencyModelState: State<CurrencyModel>,
  onAccountNameChange: (model: String) -> Unit,
  onIconClick: (model: AccountIconModel) -> Unit,
  onColorClick: (model: ColorModel) -> Unit,
  onCurrencyClick: (model: CurrencyModel) -> Unit,
  onCreateAccountClick: () -> Unit,
  openBottomSheet: (BottomSheetType) -> Unit,
  isCreateButtonEnabled: State<Boolean>,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .imePadding()
      .verticalScroll(rememberScrollState())
      .padding(paddingValues)
      .padding(Dimens.margin_large_x),
    verticalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
  ) {
    AccountName(
      accountName = nameState,
      onAccountNameChange = onAccountNameChange,
    )
    IconRow(
      icon = iconState,
      setIcon = onIconClick,
      openSheet = openBottomSheet,
    )
    ColorRow(
      color = colorState,
      setColor = onColorClick,
      openSheet = openBottomSheet,
    )
    // Todo ask Anton
    Spacer(modifier = Modifier.height(Dimens.margin_large_x))
    InitialBalanceRow(
      initialBalanceState = initialBalanceState,
      currencyState = currencyState,
      equalButtonState = equalButtonState,
      initialBalanceActions = initialBalanceActions,
      decimalSeparator = decimalSeparator,
      openSheet = openBottomSheet,
    )
    CurrencyRow(
      openSheet = openBottomSheet,
      setCurrency = onCurrencyClick,
      selectedCurrency = currencyModelState,
    )
    ExpeButton(
      textResId = AppR.string.create,
      onClick = onCreateAccountClick,
      enabled = isCreateButtonEnabled.value,
    )
  }
}

@Composable
private fun AccountName(
  accountName: State<String>,
  onAccountNameChange: (name: String) -> Unit,
) {
  val name = accountName.value
  ExpeTextField(
    label = stringResource(id = R.string.account_name),
    text = name,
    onValueChange = onAccountNameChange,
  )
}

@Composable
private fun IconRow(
  icon: State<AccountIconModel>,
  setIcon: (model: AccountIconModel) -> Unit,
  openSheet: (BottomSheetType) -> Unit,
) {
  SelectRowWithIcon(
    label = stringResource(id = R.string.icon),
    imageVector = icon.value.imageVector,
    onClick = {
      openSheet(
        BottomSheetType.Icon(
          selectedIcon = icon.value,
          onSelectIcon = setIcon,
        )
      )
    },
  )
}

@Composable
private fun ColorRow(
  color: State<ColorModel>,
  setColor: (model: ColorModel) -> Unit,
  openSheet: (BottomSheetType) -> Unit,
) {
  val colorState = color.value
  SelectRow(
    label = stringResource(id = R.string.color),
    onClick = {
      openSheet(
        BottomSheetType.Color(
          selectedColor = colorState,
          onSelectColor = setColor,
        )
      )
    },
    endLayout = {
      Box(
        modifier = Modifier
          .size(Dimens.icon_size)
          .aspectRatio(1f)
          .clip(shape = CircleShape)
          .background(color = colorState.color)
      )
    },
  )
}

@Composable
private fun InitialBalanceRow(
  initialBalanceState: State<String>,
  currencyState: State<String>,
  equalButtonState: State<EqualButtonState>,
  initialBalanceActions: NumKeyboardActions.InitialBalanceActions,
  decimalSeparator: State<String>,
  openSheet: (BottomSheetType) -> Unit,
) {

  if (IS_DEBUG_CREATE_ACCOUNT_BALANCE_BOTTOM_SHEET) {
    LaunchedEffect(key1 = Unit) {
      delay(200)
      openSheet(
        BottomSheetType.Calculator(
          initialBalanceActions = initialBalanceActions,
          decimalSeparator = decimalSeparator.value
        ).apply {
          textState = initialBalanceState
          this.equalButtonState = equalButtonState
          this.currencyState = currencyState
        }
      )
    }
  }

  SelectRowWithText(
    label = stringResource(id = R.string.balance),
    text = initialBalanceState.value,
    textStyle = MaterialTheme.typography.bodyLarge,
    onClick = {
      openSheet(
        BottomSheetType.Calculator(
          initialBalanceActions = initialBalanceActions,
          decimalSeparator = decimalSeparator.value,
        ).apply {
          textState = initialBalanceState
          this.equalButtonState = equalButtonState
          this.currencyState = currencyState
        }
      )
    },
  )
}

@Composable
private fun CurrencyRow(
  openSheet: (BottomSheetType) -> Unit,
  setCurrency: (model: CurrencyModel) -> Unit,
  selectedCurrency: State<CurrencyModel>,
) {
  val currency = selectedCurrency.value
  SelectRowWithText(
    label = stringResource(id = R.string.currency),
    text = currency.currencySymbol,
    textStyle = MaterialTheme.typography.bodyLarge,
    onClick = {
      openSheet(
        BottomSheetType.Currency(
          selectedCurrency = currency,
          onSelectCurrency = setCurrency,
        )
      )
    },
  )
}

@Composable
private fun CurrenciesBottomSheet(
  currencies: List<CurrencyModel>,
  onSelectCurrency: (currencyModel: CurrencyModel) -> Unit,
  onCloseClick: () -> Unit,
) {
  ExpeBottomSheet(
    titleResId = R.string.currency,
    onCloseClick = onCloseClick,
    content = {
      LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
          items = currencies,
          key = { item: CurrencyModel -> item.id },
          contentType = { _ -> "currencies" },
        ) { CurrencyItem(currencyModel = it, onSelectCurrency) }
        item(key = "bottom_spacer", contentType = "spacer") {
          Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
        }
      }
    },
  )
}

@Composable
private fun CurrencyItem(
  currencyModel: CurrencyModel,
  onCurrencySelect: (currencyModel: CurrencyModel) -> Unit,
) {
  Column(
    modifier = Modifier.clickable(onClick = { onCurrencySelect(currencyModel) })
  ) {
    Row(
      modifier = Modifier.padding(Dimens.margin_large_x),
      horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x)
    ) {
      Text(
        text = currencyModel.currencyName, style = MaterialTheme.typography.bodyLarge.copy(
          color = MaterialTheme.colorScheme.secondary,
          fontWeight = FontWeight.Bold
        )
      )
      Text(
        text = currencyModel.currencySymbol,
        style = MaterialTheme.typography.bodyLarge
      )
    }
    ExpeDivider()
  }
}

@Composable
private fun IconsBottomSheet(
  icons: List<AccountIconModel>,
  selectedIcon: AccountIconModel,
  onIconSelect: (color: AccountIconModel) -> Unit,
  onCloseClick: () -> Unit,
) {
  ExpeBottomSheet(
    titleResId = R.string.icon,
    onCloseClick = onCloseClick,
    content = {
      Column(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
          columns = GridCells.FixedSize(ITEM_FIXED_SIZE_DP.dp),
          horizontalArrangement = Arrangement.SpaceAround,
        ) {
          itemsIndexed(
            items = icons,
            key = { _, item: AccountIconModel -> item.id },
            contentType = { _, _ -> "icons" },
          ) { index, item ->
            IconItem(
              icon = item,
              isSelected = item == selectedIcon,
              onIconSelect = onIconSelect,
            )
          }
        }
      }
    },
  )
}

@Composable
private fun IconItem(
  icon: AccountIconModel,
  isSelected: Boolean,
  onIconSelect: (icon: AccountIconModel) -> Unit,
) {
  val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = SELECTED_ITEM_ALPHA_BORDER)

  Surface {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .aspectRatio(1f)
        .padding(Dimens.margin_small_xx)
        .clip(CircleShape)
        .clickable { onIconSelect(icon) }
        .conditional(isSelected) {
          border(
            width = SELECTED_ICON_BORDER_WIDTH.dp,
            color = borderColor,
            shape = CircleShape,
          )
        }
    ) {
      Icon(
        imageVector = icon.imageVector,
        contentDescription = icon.name,
      )
    }
  }
}

@Composable
private fun ColorsBottomSheet(
  colors: List<ColorModel>,
  selectedColor: ColorModel,
  onColorSelect: (color: ColorModel) -> Unit,
  onCloseClick: () -> Unit,
) {
  ExpeBottomSheet(
    titleResId = R.string.color,
    onCloseClick = onCloseClick,
    content = {
      Column(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
          columns = GridCells.FixedSize(ITEM_FIXED_SIZE_DP.dp),
          horizontalArrangement = Arrangement.SpaceAround,
        ) {
          items(
            items = colors,
            key = { item: ColorModel -> item.id },
            contentType = { _ -> "colors" },
          ) {
            ColorItem(
              color = it,
              isSelected = it == selectedColor,
              onColorSelect = onColorSelect,
            )
          }
        }
      }
    },
  )
}

@Composable
private fun ColorItem(
  color: ColorModel,
  isSelected: Boolean,
  onColorSelect: (color: ColorModel) -> Unit,
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .padding(Dimens.margin_small_xx)
      .clip(shape = CircleShape)
      .clickable { onColorSelect(color) }
      .conditional(isSelected) {
        border(
          width = SELECTED_COLOR_BORDER_WIDTH.dp,
          color = color.color.copy(alpha = SELECTED_ITEM_ALPHA_BORDER),
          shape = CircleShape,
        )
      },
  ) {
    Box(
      modifier = Modifier
        .padding(Dimens.margin_large_x)
        .aspectRatio(1f)
        .clip(shape = CircleShape)
        .background(color = color.color),
    )
  }
}

@Composable
private fun BottomSheetContent(
  type: BottomSheetType?,
  hideBottomSheet: () -> Unit,
) {
  when (type) {
    is BottomSheetType.Currency -> {
      CurrenciesBottomSheet(
        currencies = CurrencyModel.values().toList(),
        onSelectCurrency = {
          type.onSelectCurrency(it)
          hideBottomSheet()
        },
        onCloseClick = hideBottomSheet,
      )
    }

    is BottomSheetType.Color -> {
      ColorsBottomSheet(
        colors = ColorModel.values().toList(),
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
        icons = AccountIconModel.values().toList(),
        onIconSelect = {
          type.onSelectIcon(it)
          hideBottomSheet()
        },
        onCloseClick = hideBottomSheet,
        selectedIcon = type.selectedIcon,
      )
    }

    is BottomSheetType.Calculator -> {
      type.textState?.let {
        InitialBalanceBS(
          text = it,
          initialBalanceActions = type.initialBalanceActions,
          equalButtonState = checkNotNull(type.equalButtonState),
          decimalSeparator = type.decimalSeparator,
          currency = checkNotNull(type.currencyState),
        )
      }
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

  ExpensesTrackerTheme {
    CreateAccountScreen(
      state = state,
      onAccountNameChange = {},
      onIconClick = {},
      onColorClick = {},
      onCurrencyClick = {},
      onCreateAccountClick = {},
      onNavigationClick = {},
      initialBalanceActions = NumKeyboardActions.InitialBalanceActions.dummyInitialBalanceActions()
    )
  }
}