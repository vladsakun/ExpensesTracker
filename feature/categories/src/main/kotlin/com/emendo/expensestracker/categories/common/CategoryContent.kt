package com.emendo.expensestracker.categories.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.ExpeTextField
import com.emendo.expensestracker.core.designsystem.component.MenuAction
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.SelectRowWithColor
import com.emendo.expensestracker.core.ui.SelectRowWithIcon
import com.emendo.expensestracker.core.ui.stringValue
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun CategoryContent(
  title: String,
  stateProvider: () -> CategoryScreenData,
  onNavigationClick: () -> Unit,
  onTitleChanged: (String) -> Unit,
  onIconSelectClick: () -> Unit,
  onColorSelectClick: () -> Unit,
  onConfirmActionClick: () -> Unit,
  confirmButtonText: String,
  additionalBottomContent: @Composable ColumnScope.() -> Unit = {},
) {
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
      ExpeTextField(
        label = stringResource(id = R.string.title),
        text = stateProvider().title.stringValue(),
        onValueChange = onTitleChanged,
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
