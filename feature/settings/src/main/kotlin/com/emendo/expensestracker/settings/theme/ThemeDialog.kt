package com.emendo.expensestracker.settings.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.theme.supportsDynamicTheming
import com.emendo.expensestracker.core.model.data.DarkThemeConfig
import com.emendo.expensestracker.core.model.data.DarkThemeConfig.*
import com.emendo.expensestracker.settings.theme.ThemeUiState.Loading
import com.emendo.expensestracker.settings.theme.ThemeUiState.Success

@Composable
fun ThemeDialog(
  onDismiss: () -> Unit,
  viewModel: ThemeViewModel = hiltViewModel(),
) {
  val settingsUiState by viewModel.themeUiState.collectAsStateWithLifecycle()
  ThemeDialog(
    onDismiss = onDismiss,
    themeUiState = settingsUiState,
    onChangeDynamicColorPreference = viewModel::updateDynamicColorPreference,
    onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
  )
}

@Composable
private fun ThemeDialog(
  themeUiState: ThemeUiState,
  supportDynamicColor: Boolean = supportsDynamicTheming(),
  onDismiss: () -> Unit,
  onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
  onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
  val configuration = LocalConfiguration.current

  /**
   * usePlatformDefaultWidth = false is use as a temporary fix to allow
   * height recalculation during recomposition. This, however, causes
   * Dialog's to occupy full width in Compact mode. Therefore max width
   * is configured below. This should be removed when there's fix to
   * https://issuetracker.google.com/issues/221643630
   */
  AlertDialog(
    properties = DialogProperties(usePlatformDefaultWidth = false),
    modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
    onDismissRequest = { onDismiss() },
    title = {
      Text(
        text = stringResource(R.string.theme_dialog_title),
        style = MaterialTheme.typography.titleLarge,
      )
    },
    text = {
      HorizontalDivider()
      Column(Modifier.verticalScroll(rememberScrollState())) {
        when (themeUiState) {
          Loading -> ExpLoadingWheel()

          is Success -> {
            SettingsPanel(
              settings = themeUiState.settings,
              supportDynamicColor = supportDynamicColor,
              onChangeDynamicColorPreference = onChangeDynamicColorPreference,
              onChangeDarkThemeConfig = onChangeDarkThemeConfig,
            )
          }
        }
        HorizontalDivider(Modifier.padding(top = 8.dp))
      }
    },
    confirmButton = {
      ExpeButton(
        onClick = onDismiss,
        modifier = Modifier.padding(horizontal = 8.dp),
        textResId = R.string.theme_dialog_dismiss,
      )
    },
  )
}

// [ColumnScope] is used for using the [ColumnScope.AnimatedVisibility] extension overload composable.
@Composable
private fun ColumnScope.SettingsPanel(
  settings: UserEditableSettings,
  supportDynamicColor: Boolean,
  onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
  onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
  AnimatedVisibility(supportDynamicColor) {
    Column {
      SettingsDialogSectionTitle(text = stringResource(R.string.theme_dialog_dynamic_color_preference))
      Column(Modifier.selectableGroup()) {
        SettingsDialogThemeChooserRow(
          text = stringResource(R.string.theme_dialog_dynamic_color_yes),
          selected = settings.useDynamicColor,
          onClick = { onChangeDynamicColorPreference(true) },
        )
        SettingsDialogThemeChooserRow(
          text = stringResource(R.string.theme_dialog_dynamic_color_no),
          selected = !settings.useDynamicColor,
          onClick = { onChangeDynamicColorPreference(false) },
        )
      }
    }
  }
  SettingsDialogSectionTitle(text = stringResource(R.string.theme_dialog_dark_mode_preference))
  Column(Modifier.selectableGroup()) {
    SettingsDialogThemeChooserRow(
      text = stringResource(R.string.theme_dialog_dark_mode_config_system_default),
      selected = settings.darkThemeConfig == FOLLOW_SYSTEM,
      onClick = { onChangeDarkThemeConfig(FOLLOW_SYSTEM) },
    )
    SettingsDialogThemeChooserRow(
      text = stringResource(R.string.theme_dialog_dark_mode_config_light),
      selected = settings.darkThemeConfig == LIGHT,
      onClick = { onChangeDarkThemeConfig(LIGHT) },
    )
    SettingsDialogThemeChooserRow(
      text = stringResource(R.string.theme_dialog_dark_mode_config_dark),
      selected = settings.darkThemeConfig == DARK,
      onClick = { onChangeDarkThemeConfig(DARK) },
    )
  }
}

@Composable
private fun SettingsDialogSectionTitle(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.titleMedium,
    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
  )
}

@Composable
fun SettingsDialogThemeChooserRow(
  text: String,
  selected: Boolean,
  onClick: () -> Unit,
) {
  Row(
    Modifier
      .fillMaxWidth()
      .selectable(
        selected = selected,
        role = Role.RadioButton,
        onClick = onClick,
      )
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    RadioButton(
      selected = selected,
      onClick = null,
    )
    Spacer(Modifier.width(8.dp))
    Text(text)
  }
}

@Preview
@Composable
private fun PreviewThemeDialog() {
  ExpensesTrackerTheme {
    ThemeDialog(
      onDismiss = {},
      themeUiState = Success(
        UserEditableSettings(
          darkThemeConfig = FOLLOW_SYSTEM,
          useDynamicColor = false,
        ),
      ),
      onChangeDynamicColorPreference = {},
      onChangeDarkThemeConfig = {},
    )
  }
}

@Preview
@Composable
private fun PreviewThemeDialogLoading() {
  ExpensesTrackerTheme {
    ThemeDialog(
      onDismiss = {},
      themeUiState = Loading,
      onChangeDynamicColorPreference = {},
      onChangeDarkThemeConfig = {},
    )
  }
}