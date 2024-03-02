package com.emendo.expensestracker.accounts.common.design

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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.emendo.expensestracker.accounts.common.AccountScreenData
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.ExpeTextFieldWithRoundedBackground
import com.emendo.expensestracker.core.designsystem.component.MenuAction
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.SelectRowWithColor
import com.emendo.expensestracker.core.ui.SelectRowWithIcon
import com.emendo.expensestracker.core.ui.SelectRowWithText
import kotlinx.collections.immutable.persistentListOf

@Composable
internal inline fun <T> AccountContent(
  crossinline stateProvider: () -> AccountScreenData<T>,
  title: String,
  noinline onNavigationClick: () -> Unit,
  noinline onNameChange: (String) -> Unit,
  noinline onIconRowClick: () -> Unit,
  noinline onColorRowClick: () -> Unit,
  noinline onBalanceRowClick: () -> Unit,
  noinline onCurrencyRowClick: () -> Unit,
  noinline onConfirmClick: () -> Unit,
  shouldFocusTitleInputOnLaunch: Boolean = false,
  crossinline endContent: @Composable ColumnScope.() -> Unit,
) {
  val focusRequester = remember { FocusRequester() }
  val keyboardController = LocalSoftwareKeyboardController.current
  LaunchedEffect(shouldFocusTitleInputOnLaunch) {
    if (shouldFocusTitleInputOnLaunch) {
      focusRequester.requestFocus()
    }
  }
  ExpeScaffoldWithTopBar(
    title = title,
    onNavigationClick = onNavigationClick,
    actions = persistentListOf(
      MenuAction(
        icon = ExpeIcons.Check,
        onClick = onConfirmClick,
        contentDescription = stringResource(id = R.string.confirm),
      )
    ),
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(paddingValues)
        .padding(Dimens.margin_large_x)
        .imePadding(),
      verticalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
    ) {
      ExpeTextFieldWithRoundedBackground(
        placeholder = stringResource(id = R.string.account_name),
        text = stateProvider().name,
        onValueChange = onNameChange,
        modifier = Modifier
          .focusRequester(focusRequester)
          .onFocusChanged {
            if (it.isFocused) {
              keyboardController?.show()
            }
          },
      )
      SelectRowWithText(
        labelResId = R.string.balance,
        textProvider = { stateProvider().balance.formattedValue },
        onClick = onBalanceRowClick,
      )
      SelectRowWithIcon(
        labelResId = R.string.icon,
        imageVectorProvider = { stateProvider().icon.imageVector },
        onClick = onIconRowClick,
      )
      SelectRowWithColor(
        labelResId = R.string.color,
        colorProvider = { stateProvider().color },
        onClick = onColorRowClick,
      )
      SelectRowWithText(
        labelResId = R.string.currency,
        textProvider = { stateProvider().currency.currencySymbolOrCode },
        onClick = onCurrencyRowClick,
      )
      endContent()
    }
  }
}