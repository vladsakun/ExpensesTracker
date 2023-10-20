package com.emendo.expensestracker.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(start = true)
@Composable
internal fun SettingsRoute(
  navigator: DestinationsNavigator,
  viewModel: SettingsViewModel = hiltViewModel(),
) {
  val uiState = viewModel.state.collectAsStateWithLifecycle()

  ExpeScaffoldWithTopBar(titleResId = R.string.settings) {
    SettingsScreen(
      uiStateProvider = { uiState.value },
      onItemClick = { TODO() },
      modifier = Modifier.padding(it),
    )
  }
}

@Composable
private fun SettingsScreen(
  uiStateProvider: () -> SettingsScreenData,
  onItemClick: (SettingsItemModel) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    contentPadding = PaddingValues(vertical = Dimens.margin_large_x),
    modifier = modifier.fillMaxSize(),
  ) {
    items(
      items = uiStateProvider().settingsItems,
      key = { it.id },
      contentType = { "settingsItem" },
    ) {
      SettingsItem(
        item = it,
        onClick = { onItemClick(it) },
      )
    }
  }
}

@Composable
private fun SettingsItem(
  item: SettingsItemModel,
  onClick: () -> Unit,
) {
  Column {
    Row(
      modifier = Modifier
        .clickable(onClick = onClick)
        .padding(Dimens.margin_large_x),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x)
    ) {
      Icon(
        imageVector = item.icon,
        contentDescription = null,
      )
      Text(
        text = stringResource(id = item.titleResId),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
          .weight(1f)
          .padding(end = Dimens.margin_small_x),
      )
      item.value?.let {
        Text(
          text = it.text,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.secondary,
        )
      }
    }
    ExpeDivider(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = Dimens.icon_size + Dimens.margin_large_x * 2, end = Dimens.margin_large_x)
    )
  }
}

private val SettingsItemValue.text: String
  @Composable
  @ReadOnlyComposable
  get() = when (this) {
    is SettingsItemValue.StringValue -> value
    is SettingsItemValue.StringResValue -> stringResource(id = resId)
  }