package com.emendo.expensestracker.core.ui.bottomsheet.general

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.emendo.expensestracker.core.designsystem.component.ExpeBottomSheet
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.component.VerticalSpacer
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.bottomsheet.general.ActionType.Companion.color
import com.emendo.expensestracker.core.ui.stringValue

@Composable
fun ColumnScope.GeneralBottomSheet(data: GeneralBottomSheetData) {
  with(data) {
    ExpeBottomSheet(title = title?.stringValue()) {
      VerticalSpacer(Dimens.margin_large_x)
      ExpeButton(
        text = positiveAction.title.stringValue(),
        onClick = positiveAction.action,
        colors = positiveAction.type?.color?.let {
          ButtonDefaults.buttonColors(containerColor = it)
        } ?: ButtonDefaults.buttonColors(),
        modifier = Modifier.padding(horizontal = Dimens.margin_large_x),
      )
      negativeAction?.let { negativeAction ->
        VerticalSpacer(Dimens.margin_small_x)
        ExpeButton(
          text = negativeAction.title.stringValue(),
          onClick = negativeAction.action,
          colors = ButtonDefaults.outlinedButtonColors(),
          textColor = negativeAction.type?.color,
          modifier = Modifier.padding(horizontal = Dimens.margin_large_x),
        )
      }
    }
  }
}
