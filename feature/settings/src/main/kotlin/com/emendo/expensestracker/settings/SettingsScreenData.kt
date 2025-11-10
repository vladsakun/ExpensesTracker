package com.emendo.expensestracker.settings

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.expensestracker.model.ui.TextValue
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import kotlinx.collections.immutable.ImmutableList

data class SettingsScreenData(
  val showThemeDialog: StateEvent = consumed,
  val settingsItems: ImmutableList<SettingsItemModel>,
)

data class SettingsItemModel(
  val id: Int,
  val icon: ImageVector,
  @StringRes val titleResId: Int,
  val value: TextValue? = null,
)
