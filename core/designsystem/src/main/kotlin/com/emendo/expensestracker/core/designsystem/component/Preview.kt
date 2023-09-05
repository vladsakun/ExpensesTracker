package com.emendo.expensestracker.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_NO,
  name = "Light theme",
  device = Devices.PIXEL_4_XL,
  showBackground = true,
)
@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_YES,
  name = "Dark theme",
  device = Devices.PIXEL_4_XL,
  showBackground = true,
)
annotation class ThemePreviews

@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_NO,
  name = "Light theme landscape",
  device = "spec:parent=pixel_4_xl,orientation=landscape",
  showBackground = true,
)
@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_YES,
  name = "Dark theme landscape",
  device = "spec:parent=pixel_4_xl,orientation=landscape",
  showBackground = true,
)
@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_NO,
  name = "Light theme tablet",
  device = "spec:width=1280dp,height=800dp,dpi=240",
  showBackground = true,
)
@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_YES,
  name = "Dark theme tablet",
  device = "spec:width=1280dp,height=800dp,dpi=240",
  showBackground = true,
)
annotation class DevicePreviews

@ThemePreviews
annotation class ExpePreview