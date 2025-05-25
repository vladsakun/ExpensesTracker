package com.emendo.expensestracker.categories.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusBottomShape
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusTopShape
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.emendo.expensestracker.core.ui.SelectRowWithColor
import com.emendo.expensestracker.core.ui.SelectRowWithIcon
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.model.ui.textValueOrBlank
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
  noinline onAddSubcategoryClick: () -> Unit,
  noinline onSubcategoryClick: (name: String, iconId: Int, index: Int) -> Unit,
  noinline onDeleteSubcategoryClick: (index: Int) -> Unit,
  confirmButtonText: String,
  shouldFocusTitleInputOnLaunch: Boolean = false,
  crossinline additionalBottomContent: @Composable () -> Unit = {},
) {
  val focusRequester = remember { FocusRequester() }
  val keyboardController = LocalSoftwareKeyboardController.current
  LaunchedEffect(Unit) {
    if (shouldFocusTitleInputOnLaunch && stateProvider().title.textValueOrBlank().isBlank()) {
      focusRequester.requestFocus()
      keyboardController?.show()
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
        .padding(paddingValues)
        .consumeWindowInsets(paddingValues)
        .imePadding(),
    ) {
      LazyColumn(
        contentPadding = PaddingValues(Dimens.margin_large_x),
        modifier = Modifier.weight(1f)
      ) {
        uniqueItem(key = "topContent") {
          ExpeTextFieldWithRoundedBackground(
            placeholder = stringResource(id = R.string.title),
            text = stateProvider().title.stringValue(),
            onValueChange = onTitleChanged,
            modifier = Modifier.focusRequester(focusRequester)
          )
          VerticalSpacer(Dimens.margin_large_x)
        }
        uniqueItem(key = "icon") {
          SelectRowWithIcon(
            labelResId = R.string.icon,
            imageVectorProvider = { stateProvider().icon.imageVector },
            onClick = onIconSelectClick,
          )
          VerticalSpacer(Dimens.margin_large_x)
        }
        uniqueItem(key = "color") {
          SelectRowWithColor(
            labelResId = R.string.color,
            colorProvider = { stateProvider().color },
            onClick = onColorSelectClick,
          )
          VerticalSpacer(Dimens.margin_large_x)
        }
        uniqueItem("subcategories") {
          Text(
            text = stringResource(id = R.string.category_content_subcategories),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerNormalRadiusTopShape)
              .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
              .padding(Dimens.margin_large_x)
          )
        }
        itemsIndexed(
          items = stateProvider().subcategories,
          key = { index, _ -> index },
          contentType = { _, _ -> "subcategory" },
        ) { index, subcategory ->
          Subcategory(
            title = subcategory.name,
            icon = subcategory.icon.imageVector,
            onClick = { onSubcategoryClick(subcategory.name, subcategory.icon.id, index) },
            onDeleteClick = { onDeleteSubcategoryClick(index) },
          )
        }
        uniqueItem("addSubcategory") {
          ExpeButtonWithIcon(
            titleResId = R.string.category_content_add_subcategory_action,
            icon = ExpeIcons.Add,
            onClick = onAddSubcategoryClick,
            colors = ButtonDefaults.outlinedButtonColors(),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerNormalRadiusBottomShape)
              .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
          )
          VerticalSpacer(Dimens.margin_large_x)
        }
      }
      ExpeButton(
        text = confirmButtonText,
        onClick = onConfirmActionClick,
        enabled = stateProvider().confirmButtonEnabled,
        modifier = Modifier
          .fillMaxWidth()
          .padding(Dimens.margin_large_x),
      )
      additionalBottomContent()
    }
  }
}

@Composable
private fun Subcategory(
  title: String,
  icon: ImageVector,
  onClick: () -> Unit,
  onDeleteClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
      .clickable(onClick = onClick)
      .padding(start = Dimens.margin_large_x),
    horizontalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(imageVector = icon, contentDescription = null)
    Text(
      text = title,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.weight(1f),
    )
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
      Icon(imageVector = ExpeIcons.MoreVert, contentDescription = null)
      ExpeDropdownMenu(
        expanded = expanded,
        items = persistentListOf(
          DropdownMenuItem(
            text = stringResource(id = R.string.delete),
            icon = ExpeIcons.Delete,
            onClick = {
              onDeleteClick()
              expanded = false
            }
          )
        ),
        onDismissRequest = { expanded = false },
      )
    }
  }
}
