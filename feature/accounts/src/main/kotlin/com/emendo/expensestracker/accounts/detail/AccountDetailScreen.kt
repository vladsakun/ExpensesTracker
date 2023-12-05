package com.emendo.expensestracker.accounts.detail

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.accounts.common.AccountBottomSheetContent
import com.emendo.expensestracker.accounts.common.AccountContent
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectColorScreenDestination
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectCurrencyScreenDestination
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectIconScreenDestination
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.component.ExpePreview
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.ui.bottomsheet.GeneralBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import com.emendo.expensestracker.core.ui.bottomsheet.base.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.composition.ScreenWithModalBottomSheet
import com.emendo.expensestracker.core.ui.handleValueResult
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient

@Destination
@Composable
fun AccountDetailScreen(
  navigator: DestinationsNavigator,
  @Suppress("UNUSED_PARAMETER") accountId: Long,
  colorResultRecipient: OpenResultRecipient<Int>,
  currencyResultRecipient: OpenResultRecipient<String>,
  iconResultRecipient: OpenResultRecipient<Int>,
  viewModel: AccountDetailViewModel = hiltViewModel(),
) {
  colorResultRecipient.handleValueResult(viewModel::updateColorById)
  currencyResultRecipient.handleValueResult(viewModel::updateCurrencyByCode)
  iconResultRecipient.handleValueResult(viewModel::updateIconById)

  ScreenWithModalBottomSheet(
    stateManager = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    bottomSheetContent = { type, hideBottomSheet -> BottomSheetContent(type, hideBottomSheet) },
  ) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    AccountDetailContent(
      stateProvider = state::value,
      onNavigationClick = navigator::navigateUp,
      onNameChange = remember { viewModel::setAccountName },
      onIconRowClick = remember { { navigator.navigate(SelectIconScreenDestination(viewModel.selectedIconId)) } },
      onColorRowClick = remember { { navigator.navigate(SelectColorScreenDestination(viewModel.selectedColorId)) } },
      onBalanceRowClick = remember { viewModel::showBalanceBottomSheet },
      onCurrencyRowClick = remember { { navigator.navigate(SelectCurrencyScreenDestination) } },
      onConfirmAccountDetailsClick = remember { viewModel::updateAccount },
      onDeleteClick = remember { viewModel::showConfirmDeleteAccountBottomSheet },
    )
  }
}

@Composable
private fun AccountDetailContent(
  stateProvider: () -> AccountDetailScreenData,
  onNavigationClick: () -> Unit,
  onNameChange: (String) -> Unit,
  onIconRowClick: () -> Unit,
  onColorRowClick: () -> Unit,
  onBalanceRowClick: () -> Unit,
  onCurrencyRowClick: () -> Unit,
  onConfirmAccountDetailsClick: () -> Unit,
  onDeleteClick: () -> Unit,
) {
  AccountContent(
    stateProvider = stateProvider,
    title = stringResource(id = R.string.account),
    onNavigationClick = onNavigationClick,
    onNameChange = onNameChange,
    onIconRowClick = onIconRowClick,
    onColorRowClick = onColorRowClick,
    onBalanceRowClick = onBalanceRowClick,
    onCurrencyRowClick = onCurrencyRowClick,
    onConfirmClick = onConfirmAccountDetailsClick
  ) {
    Spacer(modifier = Modifier.padding(vertical = Dimens.margin_small_x))
    ExpeButton(
      textResId = R.string.save,
      onClick = onConfirmAccountDetailsClick,
    )
    ExpeButton(
      textResId = R.string.delete,
      onClick = onDeleteClick,
      colors = ButtonDefaults.textButtonColors(),
    )
  }
}

@Composable
private fun BottomSheetContent(
  type: BottomSheetType?,
  hideBottomSheet: () -> Unit,
) {
  when (type) {
    is GeneralBottomSheetData -> GeneralBottomSheet(type)
    else -> AccountBottomSheetContent(type = type, hideBottomSheet)
  }
}

@ExpePreview
@Composable
private fun AccountDetailScreenPreview(
  @PreviewParameter(AccountDetailPreviewData::class) previewData: AccountDetailScreenData,
) {
  ExpensesTrackerTheme {
    AccountDetailContent(
      stateProvider = { previewData },
      onNameChange = {},
      onConfirmAccountDetailsClick = {},
      onNavigationClick = {},
      onBalanceRowClick = {},
      onIconRowClick = {},
      onColorRowClick = {},
      onCurrencyRowClick = {},
      onDeleteClick = {},
    )
  }
}