package com.emendo.expensestracker.categories.subcategory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.categories.destinations.CreateSubcategoryRouteDestination
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.ExpeTextFieldWithRoundedBackground
import com.emendo.expensestracker.core.designsystem.component.MenuAction
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.hideKeyboard
import com.emendo.expensestracker.core.ui.SelectRowWithIcon
import com.emendo.expensestracker.core.ui.handleValueResult
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.scope.resultRecipient
import kotlinx.collections.immutable.persistentListOf

@Destination
@Composable
fun CreateSubcategoryRoute(
  navigator: DestinationsNavigator,
  colorId: Int,
  iconResultRecipient: OpenResultRecipient<Int>,
  resultNavigator: ResultBackNavigator<CreateSubcategoryResult>,
  viewModel: CreateSubcategoryViewModel = hiltViewModel(),
) {
  iconResultRecipient.handleValueResult(viewModel::updateIcon)

  val state = viewModel.state.collectAsStateWithLifecycle()

  ExpeScaffoldWithTopBar(
    title = stringResource(R.string.create_subcategory_title),
    onNavigationClick = navigator::navigateUp,
    actions = persistentListOf(
      MenuAction(
        icon = ExpeIcons.Check,
        onClick = { resultNavigator.navigateBack(viewModel.getResult()) },
        contentDescription = stringResource(id = R.string.confirm),
      )
    )
  ) { paddingValues ->
    CreateSubcategoryScreen(
      stateProvider = state::value,
      commandProcessor = viewModel::processCommand,
      onIconSelectClick = { navigator.navigate(viewModel.getSelectIconScreenRoute()) },
      onConfirmActionClick = { resultNavigator.navigateBack(viewModel.getResult()) },
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

@Composable
private fun CreateSubcategoryScreen(
  stateProvider: () -> CreateSubcategoryUiState,
  commandProcessor: (CreateSubcategoryCommand) -> Unit,
  onIconSelectClick: () -> Unit,
  onConfirmActionClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val focusRequester = remember { FocusRequester() }
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current
  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
  ) {
    ExpeTextFieldWithRoundedBackground(
      placeholder = stringResource(id = R.string.title),
      text = stateProvider().title,
      onValueChange = { commandProcessor(UpdateTitleSubcategoryCommand(it)) },
      modifier = Modifier
        .focusRequester(focusRequester)
        .onFocusChanged {
          if (it.isFocused) {
            keyboardController?.show()
          }
        }
    )
    SelectRowWithIcon(
      labelResId = R.string.icon,
      imageVectorProvider = { stateProvider().icon.imageVector },
      onClick = onIconSelectClick,
    )
    Spacer(modifier = Modifier.weight(1f))
    ExpeButton(
      text = stringResource(R.string.confirm),
      onClick = {
        hideKeyboard(keyboardController, focusManager)
        onConfirmActionClick()
      },
      enabled = stateProvider().confirmButtonEnabled,
      modifier = Modifier.padding(bottom = Dimens.margin_large_x)
    )
  }
}

@Composable
fun DestinationScope<*>.subcategoryResultRecipient() =
  resultRecipient<CreateSubcategoryRouteDestination, CreateSubcategoryResult>()