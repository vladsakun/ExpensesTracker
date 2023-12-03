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
import com.emendo.expensestracker.accounts.common.AccountBottomSheetContent
import com.emendo.expensestracker.accounts.common.AccountContent
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.component.ExpePreview
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseScreenWithModalBottomSheetWithViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
internal fun CreateAccountRoute(
  navigator: DestinationsNavigator,
  viewModel: CreateAccountViewModel = hiltViewModel(),
) {
  BaseScreenWithModalBottomSheetWithViewModel(
    viewModel = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    bottomSheetContent = { type, hideBottomSheet -> AccountBottomSheetContent(type, hideBottomSheet) },
  ) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    CreateAccountContent(
      stateProvider = state::value,
      onNavigationClick = navigator::navigateUp,
      onNameChange = remember { viewModel::setAccountName },
      onIconRowClick = remember { viewModel::openIconBottomSheet },
      onColorRowClick = remember { viewModel::openColorBottomSheet },
      onBalanceRowClick = remember { viewModel::showBalanceBottomSheet },
      onCurrencyRowClick = remember { viewModel::openCurrencyBottomSheet },
      onCreateAccountClick = remember { viewModel::createNewAccount },
    )
  }
}

@Composable
private fun CreateAccountContent(
  stateProvider: () -> CreateAccountScreenData,
  onNavigationClick: () -> Unit,
  onNameChange: (String) -> Unit,
  onIconRowClick: () -> Unit,
  onColorRowClick: () -> Unit,
  onBalanceRowClick: () -> Unit,
  onCurrencyRowClick: () -> Unit,
  onCreateAccountClick: () -> Unit,
) {
  AccountContent(
    stateProvider = stateProvider,
    title = stringResource(id = R.string.create_account),
    onNavigationClick = onNavigationClick,
    onNameChange = onNameChange,
    onIconRowClick = onIconRowClick,
    onColorRowClick = onColorRowClick,
    onBalanceRowClick = onBalanceRowClick,
    onCurrencyRowClick = onCurrencyRowClick,
    onConfirmClick = onCreateAccountClick,
  ) {
    Spacer(modifier = Modifier.padding(vertical = Dimens.margin_small_x))
    ExpeButton(
      textResId = R.string.create,
      onClick = onCreateAccountClick,
      enabled = stateProvider().isCreateAccountButtonEnabled,
    )
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