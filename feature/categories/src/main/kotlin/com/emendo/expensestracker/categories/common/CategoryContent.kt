package com.emendo.expensestracker.categories.common

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
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.ExpeTextFieldWithRoundedBackground
import com.emendo.expensestracker.core.designsystem.component.MenuAction
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.SelectRowWithColor
import com.emendo.expensestracker.core.ui.SelectRowWithIcon
import com.emendo.expensestracker.core.ui.stringValue
import kotlinx.collections.immutable.persistentListOf

@Composable
internal inline fun CategoryContent(
  title: String,
  crossinline stateProvider: () -> CategoryScreenData,
  noinline onNavigationClick: () -> Unit,
  noinline onTitleChanged: (String) -> Unit,
  noinline onIconSelectClick: () -> Unit,
  noinline onColorSelectClick: () -> Unit,
  noinline onConfirmActionClick: () -> Unit,
  confirmButtonText: String,
  shouldFocusTitleInputOnLaunch: Boolean = false,
  crossinline additionalBottomContent: @Composable ColumnScope.() -> Unit = {},
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
        onClick = onConfirmActionClick,
        enabled = stateProvider().confirmButtonEnabled,
        contentDescription = stringResource(id = R.string.confirm),
      )
    )
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .verticalScroll(rememberScrollState())
        .padding(paddingValues)
        .padding(Dimens.margin_large_x),
      verticalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
    ) {
      ExpeTextFieldWithRoundedBackground(
        placeholder = stringResource(id = R.string.title),
        text = stateProvider().title.stringValue(),
        onValueChange = onTitleChanged,
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
      SelectRowWithColor(
        labelResId = R.string.color,
        colorProvider = { stateProvider().color },
        onClick = onColorSelectClick,
      )
      Spacer(modifier = Modifier.height(Dimens.margin_small_x))
      ExpeButton(
        text = confirmButtonText,
        onClick = onConfirmActionClick,
        enabled = stateProvider().confirmButtonEnabled,
      )
      additionalBottomContent()
    }
  }
}
