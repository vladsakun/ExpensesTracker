package com.emendo.expensestracker.accounts.create

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.accounts.common.UiState
import com.emendo.expensestracker.accounts.common.design.AccountBottomSheetContent
import com.emendo.expensestracker.accounts.common.design.AccountContent
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.component.ExpePreview
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.ui.bottomsheet.base.ScreenWithModalBottomSheet
import com.emendo.expensestracker.core.ui.handleValueResult
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient

@Destination
@Composable
fun CreateAccountRoute(
  navigator: DestinationsNavigator,
  colorResultRecipient: OpenResultRecipient<Int>,
  currencyResultRecipient: OpenResultRecipient<String>,
  iconResultRecipient: OpenResultRecipient<Int>,
  viewModel: CreateAccountViewModel = hiltViewModel(),
) {
  colorResultRecipient.handleValueResult(viewModel::updateColorById)
  currencyResultRecipient.handleValueResult(viewModel::updateCurrencyByCode)
  iconResultRecipient.handleValueResult(viewModel::updateIconById)

  ScreenWithModalBottomSheet(
    stateManager = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    bottomSheetContent = { type -> AccountBottomSheetContent(type) },
  ) {
    val stateState = viewModel.state.collectAsStateWithLifecycle()

    CreateAccountContent(
      stateProvider = stateState::value,
      onNavigationClick = navigator::navigateUp,
      onNameChange = remember { viewModel::setAccountName },
      onIconRowClick = remember { viewModel::openSelectIconScreen },
      onColorRowClick = remember { viewModel::openSelectColorScreen },
      onBalanceRowClick = remember { viewModel::showBalanceBottomSheet },
      onCurrencyRowClick = remember { viewModel::openSelectCurrencyScreen },
      onCreateAccountClick = remember { viewModel::createNewAccount },
    )
  }
}

@Composable
private fun CreateAccountContent(
  stateProvider: () -> UiState<CreateAccountScreenData>,
  onNavigationClick: () -> Unit,
  onNameChange: (String) -> Unit,
  onIconRowClick: () -> Unit,
  onColorRowClick: () -> Unit,
  onBalanceRowClick: () -> Unit,
  onCurrencyRowClick: () -> Unit,
  onCreateAccountClick: () -> Unit,
) {
  when (val state = stateProvider()) {
    is UiState.Data<CreateAccountScreenData> -> {
      AccountContent(
        stateProvider = { state.data },
        title = stringResource(id = R.string.create_account),
        onNavigationClick = onNavigationClick,
        onNameChange = onNameChange,
        onIconRowClick = onIconRowClick,
        onColorRowClick = onColorRowClick,
        onBalanceRowClick = onBalanceRowClick,
        onCurrencyRowClick = onCurrencyRowClick,
        onConfirmClick = onCreateAccountClick,
        shouldFocusTitleInputOnLaunch = true,
      ) {
        Spacer(modifier = Modifier.padding(vertical = Dimens.margin_small_x))
        ExpeButton(
          textResId = R.string.create,
          onClick = onCreateAccountClick,
          enabled = state.data.confirmEnabled,
        )
      }
    }

    is UiState.Loading, is UiState.Error -> Unit
  }
}

@ExpePreview
@Composable
private fun CreateAccountScreenPreview(
  @PreviewParameter(CreateAccountPreviewData::class) previewData: CreateAccountScreenData,
) {
  ExpensesTrackerTheme {
    CreateAccountContent(
      stateProvider = { UiState.Data(previewData) },
      onNameChange = {},
      onCreateAccountClick = {},
      onNavigationClick = {},
      onBalanceRowClick = {},
      onIconRowClick = {},
      onColorRowClick = {},
      onCurrencyRowClick = {},
    )
  }
}