package com.emendo.expensestracker.accounts.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.component.ExpePreview
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.ExpeTextField
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.ui.SelectRow
import com.emendo.expensestracker.core.ui.SelectRowWithColor
import com.emendo.expensestracker.core.ui.SelectRowWithIcon
import com.emendo.expensestracker.core.ui.bottomsheet.ColorsBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.CurrenciesBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.IconsBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseScreenWithModalBottomSheetWithViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import com.emendo.expensestracker.core.ui.bottomsheet.calculator.InitialBalanceBS
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun CreateAccountRoute(
  navigator: DestinationsNavigator,
  viewModel: CreateAccountViewModel = hiltViewModel(),
) {
  val onAccountNameChange = remember { { model: String -> viewModel.setAccountName(model) } }
  val state = viewModel.state.collectAsStateWithLifecycle()

  BaseScreenWithModalBottomSheetWithViewModel(
    viewModel = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    content = {
      CreateAccountContent(
        stateProvider = state::value,
        onAccountNameChange = onAccountNameChange,
        onCreateAccountClick = viewModel::createNewAccount,
        onNavigationClick = navigator::navigateUp,
        onInitialBalanceRowClick = viewModel::onInitialBalanceClick,
        onIconRowClick = viewModel::onIconRowClick,
        onColorRowClick = viewModel::onColorRowClick,
        onCurrencyRowClick = viewModel::onCurrencyRowClick,
      )
    },
    bottomSheetContent = { type, hideBottomSheet ->
      BottomSheetContent(type, hideBottomSheet)
    },
  )
}

@Composable
private fun CreateAccountContent(
  stateProvider: () -> CreateAccountScreenData,
  onNavigationClick: () -> Unit,
  onAccountNameChange: (model: String) -> Unit,
  onIconRowClick: () -> Unit,
  onColorRowClick: () -> Unit,
  onInitialBalanceRowClick: () -> Unit,
  onCurrencyRowClick: () -> Unit,
  onCreateAccountClick: () -> Unit,
) {
  val isCreateButtonEnabled = remember { derivedStateOf { stateProvider().isCreateAccountButtonEnabled } }
  val scrollState = rememberScrollState()
  val labelModifier: @Composable RowScope.() -> Modifier = remember { { Modifier.weight(1f) } }

  ExpeScaffoldWithTopBar(
    titleResId = R.string.create_account,
    onNavigationClick = onNavigationClick,
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
      ExpeTextField(
        label = stringResource(id = R.string.account_name),
        text = stateProvider().accountName,
        onValueChange = onAccountNameChange,
      )
      SelectRowWithIcon(
        labelResId = R.string.icon,
        imageVectorProvider = { stateProvider().icon.imageVector },
        onClick = onIconRowClick,
      )
      SelectRowWithColor(
        labelResId = R.string.color,
        colorProvider = { stateProvider().color.darkColor },
        onClick = onColorRowClick,
      )
      //       Todo ask Anton
      //       Spacer(modifier = Modifier.height(200.dp))
      SelectRow(
        labelResId = R.string.balance,
        onClick = onInitialBalanceRowClick,
        labelModifier = labelModifier,
        endLayout = {
          Text(
            text = stateProvider().initialBalance,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
          )
        }
      )
      SelectRow(
        labelResId = R.string.currency,
        onClick = onCurrencyRowClick,
        labelModifier = labelModifier,
        endLayout = {
          Text(
            text = stateProvider().currency.currencySymbolOrCode,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
          )
        }
      )
      ExpeButton(
        textResId = R.string.create,
        onClick = onCreateAccountClick,
        enabled = isCreateButtonEnabled.value,
      )
    }
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
        onSelectCurrency = {
          type.onSelectCurrency(it)
          hideBottomSheet()
        },
        onCloseClick = hideBottomSheet,
        currencies = type.currencies,
      )
    }

    is BottomSheetType.Color -> {
      ColorsBottomSheet(
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
        onIconSelect = {
          type.onSelectIcon(it)
          hideBottomSheet()
        },
        onCloseClick = hideBottomSheet,
        selectedIcon = type.selectedIcon,
      )
    }

    is BottomSheetType.InitialBalance -> {
      val text = type.text.collectAsStateWithLifecycle()
      val equalButtonState = type.equalButtonState.collectAsStateWithLifecycle()
      InitialBalanceBS(
        text = text,
        actions = type.actions,
        equalButtonState = equalButtonState,
        decimalSeparator = type.decimalSeparator,
        currency = type.currency,
      )
    }
  }
}

@ExpePreview
@Composable
private fun CreateAccountScreenPreview(
  @PreviewParameter(CreateAccountPreviewData::class) previewData: CreateAccountScreenData,
) {
  ExpensesTrackerTheme {
    CreateAccountContent(
      stateProvider = { previewData },
      onAccountNameChange = {},
      onCreateAccountClick = {},
      onNavigationClick = {},
      onInitialBalanceRowClick = {},
      onIconRowClick = {},
      onColorRowClick = {},
      onCurrencyRowClick = {},
    )
  }
}