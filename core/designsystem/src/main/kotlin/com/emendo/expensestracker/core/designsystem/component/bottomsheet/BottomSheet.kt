package com.emendo.expensestracker.core.designsystem.component.bottomsheet

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons
import com.emendo.expensestracker.core.designsystem.theme.Dimens

@Composable
fun ExpeBottomSheet(
  titleLayout: @Composable (modifier: Modifier) -> Unit,
  content: @Composable ColumnScope.() -> Unit,
  onCloseClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier.fillMaxSize()) {
    Column {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(Dimens.margin_small_x),
        horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(onClick = onCloseClick) {
          Icon(
            imageVector = ExpIcons.Close,
            contentDescription = stringResource(id = R.string.close),
            tint = MaterialTheme.colorScheme.onSurface,
          )
        }
        titleLayout(Modifier.weight(1f))
        Spacer(modifier = Modifier.width(48.dp))
      }
      Surface {
        content()
      }
    }
  }
}

@Composable
fun ExpeBottomSheet(
  @StringRes titleResId: Int,
  content: @Composable ColumnScope.() -> Unit,
  onCloseClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  ExpeBottomSheet(
    titleLayout = {
      Text(
        text = stringResource(id = titleResId),
        modifier = it,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
      )
    },
    content = content,
    onCloseClick = onCloseClick,
    modifier = modifier,
  )
}