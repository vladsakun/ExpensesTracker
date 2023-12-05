package com.emendo.expensestracker.core.ui.bottomsheet.general

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.emendo.expensestracker.core.designsystem.component.ExpeBottomSheet
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.bottomsheet.general.ActionType.Companion.color
import com.emendo.expensestracker.core.ui.stringValue

@Composable
fun ColumnScope.GeneralBottomSheet(data: GeneralBottomSheetData) {
  ExpeBottomSheet(
    title = data.title?.stringValue(),
    modifier = Modifier
      .padding(bottom = Dimens.margin_large_x)
      .padding(horizontal = Dimens.margin_large_x),
  ) {
    Spacer(modifier = Modifier.height(Dimens.margin_large_x))
    ExpeButton(
      text = data.positiveAction.title.stringValue(),
      onClick = data.positiveAction.action,
      colors = data.positiveAction.type?.color?.let {
        ButtonDefaults.buttonColors(containerColor = it)
      } ?: ButtonDefaults.buttonColors()
    )
    data.negativeAction?.let { negativeAction ->
      Spacer(modifier = Modifier.height(Dimens.margin_small_x))
      ExpeButton(
        text = negativeAction.title.stringValue(),
        onClick = negativeAction.action,
        colors = ButtonDefaults.outlinedButtonColors(),
        textColor = negativeAction.type?.color,
      )
    }
  }
}
