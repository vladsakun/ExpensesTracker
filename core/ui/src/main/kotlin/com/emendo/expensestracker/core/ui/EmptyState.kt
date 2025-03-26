package com.emendo.expensestracker.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.theme.Dimens

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
  Card(modifier = modifier) {
    Icon(
      imageVector = ExpeIcons.Empty,
      contentDescription = "Empty state",
      modifier = Modifier
        .size(80.dp)
        .padding(Dimens.margin_small_x)
        .align(Alignment.CenterHorizontally),
    )
    Text(
      text = stringResource(id = R.string.empty_general_title),
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier
        .padding(
          top = Dimens.margin_small_xx,
          start = Dimens.margin_small_x,
          end = Dimens.margin_small_x,
        )
        .align(Alignment.CenterHorizontally),
    )
    Text(
      text = stringResource(id = R.string.empty_general_message),
      style = MaterialTheme.typography.bodySmall,
      modifier = Modifier
        .padding(
          top = Dimens.margin_small_xx,
          bottom = Dimens.margin_small_x,
          start = Dimens.margin_small_x,
          end = Dimens.margin_small_x,
        )
        .align(Alignment.CenterHorizontally),
    )
  }
}