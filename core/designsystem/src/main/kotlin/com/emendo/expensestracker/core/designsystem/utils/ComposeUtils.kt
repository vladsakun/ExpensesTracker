package com.emendo.expensestracker.core.designsystem.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import com.emendo.expensestracker.core.designsystem.theme.Dimens

val ExpeBottomSheetShape = RoundedCornerShape(
  topStart = Dimens.corner_radius_normal,
  topEnd = Dimens.corner_radius_normal,
)

val RoundedCornerNormalRadiusShape = RoundedCornerShape(Dimens.corner_radius_normal)
val RoundedCornerNormalRadiusTopShape = RoundedCornerShape(
  topStart = Dimens.corner_radius_normal,
  topEnd = Dimens.corner_radius_normal,
)
val RoundedCornerNormalRadiusBottomShape = RoundedCornerShape(
  bottomStart = Dimens.corner_radius_normal,
  bottomEnd = Dimens.corner_radius_normal,
)
val RoundedCornerSmallRadiusShape = RoundedCornerShape(Dimens.corner_radius_small)

fun hideKeyboard(
  keyboardController: SoftwareKeyboardController?,
  focusManager: FocusManager,
) {
  keyboardController?.hide()
  focusManager.clearFocus()
}