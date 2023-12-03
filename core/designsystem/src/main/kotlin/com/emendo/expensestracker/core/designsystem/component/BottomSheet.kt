package com.emendo.expensestracker.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.theme.Dimens

@Composable
fun ExpeBottomSheet(
  titleLayout: @Composable (modifier: Modifier) -> Unit,
  modifier: Modifier = Modifier,
  onCloseClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  Box(
    modifier = modifier.wrapContentSize()
  ) {
    Column {
      Row(
        modifier = Modifier
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
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .windowInsetsBottomHeight(WindowInsets.safeDrawing)
        .background(
          MaterialTheme.colorScheme
            .surfaceColorAtElevation(3.dp)
            .copy(alpha = 0.7f)
        )
        .align(Alignment.BottomCenter),
    )
  }
}

@Composable
fun ExpeBottomSheet(
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
fun ExpeBottomSheet(
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