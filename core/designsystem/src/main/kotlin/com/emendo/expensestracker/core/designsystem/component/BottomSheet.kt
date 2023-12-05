package com.emendo.expensestracker.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.theme.Dimens

@Composable
fun ColumnScope.ExpeBottomSheet(
  titleLayout: @Composable (modifier: Modifier) -> Unit,
  modifier: Modifier = Modifier,
  onCloseClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(Dimens.margin_small_x),
    horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (onCloseClick != null) {
      IconButton(onClick = onCloseClick) {
        Icon(
          imageVector = ExpeIcons.Close,
          contentDescription = stringResource(id = R.string.close),
          tint = MaterialTheme.colorScheme.onSurface,
        )
      }
    }
    titleLayout(Modifier.weight(1f))
    if (onCloseClick != null) {
      Spacer(modifier = Modifier.width(Dimens.icon_button_size))
    }
  }
  content()
}

@Composable
fun ColumnScope.ExpeBottomSheet(
  @StringRes titleResId: Int,
  content: @Composable ColumnScope.() -> Unit,
  modifier: Modifier = Modifier,
  onCloseClick: (() -> Unit)? = null,
) {
  ExpeBottomSheet(
    title = stringResource(id = titleResId),
    content = content,
    onCloseClick = onCloseClick,
    modifier = modifier,
  )
}

@Composable
fun ColumnScope.ExpeBottomSheet(
  title: String?,
  modifier: Modifier = Modifier,
  onCloseClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  ExpeBottomSheet(
    titleLayout = {
      if (title != null) {
        Text(
          text = title,
          modifier = it,
          style = MaterialTheme.typography.titleLarge,
          textAlign = TextAlign.Center,
        )
      }
    },
    content = content,
    onCloseClick = onCloseClick,
    modifier = modifier,
  )
}