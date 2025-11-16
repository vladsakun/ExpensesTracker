package com.emendo.expensestracker.budget.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.hideKeyboard
import com.emendo.expensestracker.core.ui.SelectRowWithIcon
import com.emendo.expensestracker.core.ui.SelectRowWithText
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ScreenWithModalBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.NumericKeyboardBottomSheet
import com.emendo.expensestracker.core.ui.handleValueResult
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.model.ui.NetworkViewState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient
import kotlinx.collections.immutable.persistentListOf

@Destination
@Composable
fun CreateBudgetRoute(
  navigator: DestinationsNavigator,
  iconResultRecipient: OpenResultRecipient<Int>,
  categoryResultRecipient: OpenResultRecipient<Long>,
  currencyResultRecipient: OpenResultRecipient<String>,
  viewModel: CreateBudgetViewModel = hiltViewModel(),
) {
  val state = viewModel.state.collectAsStateWithLifecycle()

  iconResultRecipient.handleValueResult { iconId ->
    viewModel.processCommand(UpdateIconBudgetCommand(iconId))
  }
  categoryResultRecipient.handleValueResult { category ->
    viewModel.processCommand(UpdateCategoryBudgetCommand(category))
  }
  currencyResultRecipient.handleValueResult { currencyCode ->
    viewModel.processCommand(UpdateCurrencyBudgetCommand(currencyCode))
  }

  CreateBudgetScreen(
    viewModel = viewModel,
    navigator = navigator,
    stateProvider = state::value,
    onBackClick = navigator::navigateUp,
  )
}

@Composable
private fun CreateBudgetScreen(
  viewModel: CreateBudgetViewModel,
  navigator: DestinationsNavigator,
  stateProvider: () -> NetworkViewState<CreateBudgetScreenData>,
  onBackClick: () -> Unit,
) {
  ScreenWithModalBottomSheet(
    stateManager = viewModel,
    onNavigateUpClick = onBackClick,
    bottomSheetContent = { type -> BudgetBottomSheetContent(type) },
  ) {
    ExpeScaffoldWithTopBar(
      title = stringResource(R.string.create_budget_title),
      onNavigationClick = onBackClick,
      actions = persistentListOf(
        MenuAction(
          icon = ExpeIcons.Check,
          onClick = { viewModel.createBudget() },
          contentDescription = stringResource(id = R.string.confirm),
        )
      )
    ) { paddingValues ->
      CreateBudgetScreen(
        stateProvider = stateProvider,
        commandProcessor = viewModel::processCommand,
        onIconSelectClick = { navigator.navigate(viewModel.getSelectIconScreenRoute()) },
        onCategorySelectClick = { navigator.navigate(viewModel.getSelectCategoryScreenRoute()) },
        onLimitRowClick = { viewModel.showLimitBottomSheet() },
        onCurrencySelectClick = { navigator.navigate(viewModel.getSelectCurrencyScreenRoute()) },
        onConfirmActionClick = { viewModel.createBudget() },
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .consumeWindowInsets(paddingValues)
          .imePadding()
          .verticalScroll(rememberScrollState())
          .padding(top = Dimens.margin_large_x)
          .padding(horizontal = Dimens.margin_large_x),
      )
    }
  }
}

@Composable
private fun CreateBudgetScreen(
  stateProvider: () -> NetworkViewState<CreateBudgetScreenData>,
  commandProcessor: (CreateBudgetCommand) -> Unit,
  onIconSelectClick: () -> Unit,
  onCategorySelectClick: () -> Unit,
  onLimitRowClick: () -> Unit,
  onCurrencySelectClick: () -> Unit,
  onConfirmActionClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    when (val stateValue = stateProvider()) {
      is NetworkViewState.Idle -> Unit
      is NetworkViewState.Error -> Text(text = stateValue.message.stringValue())
      is NetworkViewState.Loading -> ExpLoadingWheel(modifier = Modifier.align(Alignment.Center))
      is NetworkViewState.Success -> {
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val data = stateValue.data
        LaunchedEffect(Unit) {
          if (data.name.isBlank()) {
            focusRequester.requestFocus()
            keyboardController?.show()
          }
        }

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.margin_large_x)) {
          ExpeTextFieldWithRoundedBackground(
            placeholder = stringResource(id = R.string.title),
            text = data.name,
            onValueChange = { commandProcessor(UpdateNameBudgetCommand(it)) },
            modifier = Modifier.focusRequester(focusRequester)
          )
          SelectRowWithText(
            labelResId = R.string.limit,
            textProvider = { data.limit.formattedValue },
            onClick = onLimitRowClick,
          )
          SelectRowWithIcon(
            labelResId = R.string.icon,
            imageVectorProvider = { data.icon.imageVector },
            onClick = onIconSelectClick,
          )
          SelectRowWithText(
            labelResId = R.string.category,
            textProvider = { data.category?.name?.stringValue() ?: stringResource(R.string.not_selected) },
            onClick = onCategorySelectClick,
          )
          SelectRowWithText(
            labelResId = R.string.currency,
            textProvider = { data.currency.currencyName },
            onClick = onCurrencySelectClick,
          )
          Spacer(modifier = Modifier.weight(1f))
          ExpeButton(
            text = stringResource(R.string.confirm),
            onClick = {
              hideKeyboard(keyboardController, focusManager)
              onConfirmActionClick()
            },
            enabled = data.confirmButtonEnabled,
            modifier = Modifier.padding(bottom = Dimens.margin_large_x)
          )
        }
      }
    }
  }
}

@Composable
private fun BudgetBottomSheetContent(
  type: BottomSheetData?,
) {
  when (type) {
    is BudgetLimitBottomSheetData -> {
      val text = type.value.collectAsStateWithLifecycle()
      val equalButtonState = type.equalButtonState.collectAsStateWithLifecycle()

      NumericKeyboardBottomSheet(
        textStateProvider = text::value,
        currency = type.currency,
        equalButtonStateProvider = equalButtonState::value,
        actions = type.actions,
        numericKeyboardActions = type.numericKeyboardActions,
        decimalSeparator = type.decimalSeparator,
      )
    }
  }
}
