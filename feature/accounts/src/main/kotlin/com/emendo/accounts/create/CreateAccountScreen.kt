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
import com.emendo.expensestracker.core.data.model.AccountIconModel
import com.emendo.expensestracker.core.data.model.ColorModel
import com.emendo.expensestracker.core.data.model.CurrencyModel
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.component.bottomsheet.ExpeBottomSheet
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.utils.*
import com.emendo.expensestracker.feature.accounts.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import com.emendo.expensestracker.core.app.resources.R as AppR

private const val TAG = "CreateAccountScreen"
private val SELECTED_ICON_BORDER_WIDTH = 3.dp
private val SELECTED_COLOR_BORDER_WIDTH = 3.dp

@Destination
@Composable
fun CreateAccountRoute(
  navigator: DestinationsNavigator,
  viewModel: CreateAccountViewModel = hiltViewModel()
) {
  CreateAccountScreen(
    state = viewModel.state.collectAsStateWithLifecycle(),
    onNavigationClick = navigator::navigateUp,
    onAccountNameChange = viewModel::setAccountName,
    onIconClick = viewModel::setIcon,
    onColorClick = viewModel::setColor,
    onCurrencyClick = viewModel::setCurrency,
    onCreateAccountClick = viewModel::createNewAccount,
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
) {
  val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  val nameTextFieldState by rememberSaveable(stateSaver = AccountNameStateSaver) {
    mutableStateOf(AccountState())
  }

  val coroutineScope = rememberCoroutineScope()
  val scaffoldState = rememberBottomSheetScaffoldState(SheetState(skipPartiallyExpanded = true))
  var currentBottomSheet: BottomSheetType? by remember { mutableStateOf(null) }

  val keyboardController = LocalSoftwareKeyboardController.current
  val focusManager = LocalFocusManager.current

  val closeBottomSheet: () -> Unit = {
    hideKeyboard(keyboardController, focusManager)
    coroutineScope.launch {
      scaffoldState.bottomSheetState.hide()
    }
  }

  val openBottomSheet: (BottomSheetType) -> Unit = {
    hideKeyboard(keyboardController, focusManager)
    currentBottomSheet = it
    coroutineScope.launch {
      scaffoldState.bottomSheetState.expand()
    }
  }

  BackHandler {
    when {
      scaffoldState.bottomSheetState.isVisible -> closeBottomSheet()
      else -> onNavigationClick()
    }
  }

  BottomSheetScaffold(
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
    scaffoldState = scaffoldState,
    sheetContent = { BottomSheetContent(currentBottomSheet, closeBottomSheet) },
    sheetShape = ExpeBottomSheetShape,
  ) { paddingValues ->
    val accountNameState = remember { derivedStateOf { state.value.accountName } }
    val iconState = remember { derivedStateOf { state.value.icon } }
    val colorState = remember { derivedStateOf { state.value.color } }
    val currencyState = remember { derivedStateOf { state.value.currency } }
    val initialBalanceState = remember { derivedStateOf { state.value.initialBalance } }

    CreateAccountContent(
      paddingValues = paddingValues,
      nameTextFieldState = nameTextFieldState,
      nameState = accountNameState,
      iconState = iconState,
      colorState = colorState,
      currencyState = currencyState,
      initialBalanceState = initialBalanceState,
      onAccountNameChange = onAccountNameChange,
      onIconClick = onIconClick,
      onColorClick = onColorClick,
      onCurrencyClick = onCurrencyClick,
      onCreateAccountClick = onCreateAccountClick,
      openBottomSheet = openBottomSheet,
    )
  }
}

@Composable
private fun CreateAccountContent(
  paddingValues: PaddingValues,
  nameTextFieldState: TextFieldState,
  nameState: State<String>,
  iconState: State<AccountIconModel>,
  colorState: State<ColorModel>,
  initialBalanceState: State<Double>,
  currencyState: State<CurrencyModel>,
  onAccountNameChange: (model: String) -> Unit,
  onIconClick: (model: AccountIconModel) -> Unit,
  onColorClick: (model: ColorModel) -> Unit,
  onCurrencyClick: (model: CurrencyModel) -> Unit,
  onCreateAccountClick: () -> Unit,
  openBottomSheet: (BottomSheetType) -> Unit,
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
      accountNameState = nameTextFieldState,
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
      onInitialBalanceClick = {},
    )
    CurrencyRow(
      openSheet = openBottomSheet,
      setCurrency = onCurrencyClick,
      selectedCurrency = currencyState,
    )
    ExpeButton(
      textResId = AppR.string.create,
      onClick = onCreateAccountClick,
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

    else -> Spacer(modifier = Modifier.fillMaxSize())
  }
}

@Composable
private fun AccountName(
  accountNameState: TextFieldState,
  accountName: State<String>,
  onAccountNameChange: (name: String) -> Unit
) {
  val name = accountName.value
  ExpeTextField(
    label = stringResource(id = R.string.account_name),
    textFieldState = accountNameState,
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
    }
  )
}

@Composable
private fun InitialBalanceRow(
  initialBalanceState: State<Double>,
  onInitialBalanceClick: () -> Unit,
) {
  val initialBalance = initialBalanceState.value.toString()
  SelectRowWithText(
    label = stringResource(id = R.string.initial_balance),
    text = initialBalance,
    onClick = onInitialBalanceClick,
  )
}

@Composable
private fun CurrencyRow(
  openSheet: (BottomSheetType) -> Unit,
  setCurrency: (model: CurrencyModel) -> Unit,
  selectedCurrency: State<CurrencyModel>
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
  onCloseClick: () -> Unit
) {
  ExpeBottomSheet(
    titleResId = R.string.currency,
    onCloseClick = onCloseClick,
    content = {
      Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
          items(
            items = currencies,
            key = { item: CurrencyModel -> item.id },
            contentType = { _ -> "currencies" },
          ) { CurrencyItem(currencyModel = it, onSelectCurrency) }
        }
      }
    },
  )
}

@Composable
private fun CurrencyItem(
  currencyModel: CurrencyModel,
  onCurrencySelect: (currencyModel: CurrencyModel) -> Unit
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
  onCloseClick: () -> Unit
) {
  ExpeBottomSheet(
    titleResId = R.string.icon,
    onCloseClick = onCloseClick,
    content = {
      Column(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
          columns = GridCells.FixedSize(70.dp),
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
  onIconSelect: (icon: AccountIconModel) -> Unit
) {
  val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)

  Surface {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .aspectRatio(1f)
        .padding(Dimens.margin_small_x)
        .clip(shape = CircleShape)
        .clickable { onIconSelect(icon) }
        .conditional(isSelected) {
          border(
            width = SELECTED_ICON_BORDER_WIDTH,
            color = borderColor,
            shape = CircleShape,
          )
        }
    ) {
      Image(
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
          columns = GridCells.FixedSize(70.dp),
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
      .padding(Dimens.margin_small_x)
      .clip(shape = CircleShape)
      .clickable { onColorSelect(color) }
      .conditional(isSelected) {
        border(
          width = SELECTED_COLOR_BORDER_WIDTH,
          color = color.color.copy(alpha = 0.2f),
          shape = CircleShape,
        )
      },
  ) {
    Box(
      modifier = Modifier
        .padding(Dimens.margin_large_x)
        .aspectRatio(1f)
        .clip(shape = CircleShape)
        .background(color = color.color)
    )
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
    )
  }
}