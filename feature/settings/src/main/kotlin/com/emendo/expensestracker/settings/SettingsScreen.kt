package com.emendo.expensestracker.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.component.ExpePreview
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.ui.handleValueResult
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.settings.theme.ThemeDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient
import de.palm.composestateevents.triggered
import kotlinx.collections.immutable.persistentListOf

@Destination(start = true)
@Composable
fun SettingsRoute(
  navigator: DestinationsNavigator,
  currencyResultRecipient: OpenResultRecipient<String>,
  viewModel: SettingsViewModel = hiltViewModel(),
) {
  currencyResultRecipient.handleValueResult(viewModel::updateCurrencyByCode)

  val uiState = viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel.navigationEvent) {
    viewModel.navigationEvent.collect { route ->
      navigator.navigate(route)
    }
  }

  if (uiState.value.showThemeDialog == triggered) {
    ThemeDialog(onDismiss = viewModel::dismissThemeDialog)
  }

  ExpeScaffoldWithTopBar(
    titleResId = R.string.settings,
    onNavigationClick = navigator::navigateUp,
  ) { paddingValues ->
    SettingsScreen(
      uiStateProvider = uiState::value,
      onItemClick = viewModel::onItemClick,
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
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
    modifier = modifier,
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
        .padding(horizontal = Dimens.margin_large_x, vertical = Dimens.margin_normal),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
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
          text = it.stringValue(),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.secondary,
        )
      }
    }
    ExpeDivider(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = Dimens.icon_size + Dimens.margin_large_x * 2, end = Dimens.margin_large_x),
    )
  }
}

@ExpePreview
@Composable
private fun SettingsScreenPreview() {
  ExpensesTrackerTheme {
    SettingsScreen(
      uiStateProvider = { SettingsScreenData(settingsItems = persistentListOf()) },
      onItemClick = {},
    )
  }
}
