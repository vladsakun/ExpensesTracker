package com.emendo.expensestracker.accounts.detail

import androidx.compose.foundation.layout.ColumnScope
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
import com.emendo.expensestracker.accounts.common.design.AccountBottomSheetContent
import com.emendo.expensestracker.accounts.common.design.AccountContent
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.component.ExpePreview
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ScreenWithModalBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.handleValueResult
import com.emendo.expensestracker.model.ui.UiState
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient

@Destination(
  deepLinks = [DeepLink(uriPattern = "https://emendo.com/accounts/$FULL_ROUTE_PLACEHOLDER")],
)
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
    bottomSheetContent = { BottomSheetContent(it) },
  ) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    AccountDetailContent(
      stateProvider = state::value,
      onNavigationClick = navigator::navigateUp,
      onNameChange = remember { viewModel::setAccountName },
      onIconRowClick = remember { { navigator.navigate(viewModel.getSelectIconScreenRoute()) } },
      onColorRowClick = remember { { navigator.navigate(viewModel.getSelectColorScreenRoute()) } },
      onBalanceRowClick = remember { viewModel::showBalanceBottomSheet },
      onCurrencyRowClick = remember { { navigator.navigate(viewModel.getSelectCurrencyScreenRoute()) } },
      onConfirmAccountDetailsClick = remember { viewModel::updateAccount },
      onDeleteClick = remember { viewModel::showConfirmDeleteAccountBottomSheet },
    )
  }
}

@Composable
private fun AccountDetailContent(
  stateProvider: () -> UiState<AccountDetailScreenData>,
  onNavigationClick: () -> Unit,
  onNameChange: (String) -> Unit,
  onIconRowClick: () -> Unit,
  onColorRowClick: () -> Unit,
  onBalanceRowClick: () -> Unit,
  onCurrencyRowClick: () -> Unit,
  onConfirmAccountDetailsClick: () -> Unit,
  onDeleteClick: () -> Unit,
) {
  when (val state = stateProvider()) {
    is UiState.Data<AccountDetailScreenData> -> {
      AccountContent(
        stateProvider = state::data,
        title = stringResource(id = R.string.account),
        onNavigationClick = onNavigationClick,
        onNameChange = onNameChange,
        onIconRowClick = onIconRowClick,
        onColorRowClick = onColorRowClick,
        onBalanceRowClick = onBalanceRowClick,
        onCurrencyRowClick = onCurrencyRowClick,
        onConfirmClick = onConfirmAccountDetailsClick,
      ) {
        ExpeButton(
          textResId = R.string.save,
          onClick = onConfirmAccountDetailsClick,
          modifier = Modifier
            .padding(top = Dimens.margin_large_x)
            .padding(horizontal = Dimens.margin_large_x),
        )
        Spacer(modifier = Modifier.padding(vertical = Dimens.margin_small_x))
        ExpeButton(
          textResId = R.string.delete,
          onClick = onDeleteClick,
          colors = ButtonDefaults.textButtonColors(),
          modifier = Modifier
            .padding(bottom = Dimens.margin_large_x)
            .padding(horizontal = Dimens.margin_large_x),
        )
      }
    }

    is UiState.Loading, is UiState.Error -> Unit
  }
}

@Composable
private fun ColumnScope.BottomSheetContent(
  type: BottomSheetData?,
) {
  when (type) {
    is GeneralBottomSheetData -> GeneralBottomSheet(type)
    else -> AccountBottomSheetContent(type = type)
  }
}

@ExpePreview
@Composable
private fun AccountDetailScreenPreview(
  @PreviewParameter(AccountDetailPreviewData::class) previewData: AccountDetailScreenData,
) {
  ExpensesTrackerTheme {
    AccountDetailContent(
      stateProvider = { UiState.Data(previewData) },
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