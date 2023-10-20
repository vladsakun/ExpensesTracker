package com.emendo.expensestracker.settings

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.ImmutableList

data class SettingsScreenData(
  val settingsItems: ImmutableList<SettingsItemModel>,
)

data class SettingsItemModel(
  val id: Int,
  val icon: ImageVector,
  @StringRes val titleResId: Int,
  val value: SettingsItemValue? = null,
)

sealed interface SettingsItemValue {
  data class StringValue(val value: String) : SettingsItemValue
  data class StringResValue(@StringRes val resId: Int) : SettingsItemValue
}