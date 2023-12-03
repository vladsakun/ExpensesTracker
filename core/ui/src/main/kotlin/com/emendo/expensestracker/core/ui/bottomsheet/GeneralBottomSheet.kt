package com.emendo.expensestracker.core.ui.bottomsheet

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.emendo.expensestracker.core.designsystem.component.ExpeBottomSheet
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.bottomsheet.base.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.stringValue

@Composable
fun GeneralBottomSheet(data: GeneralBottomSheetData) {
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
    )
    if (data.negativeAction != null) {
      Spacer(modifier = Modifier.height(Dimens.margin_small_x))
      ExpeButton(
        text = data.negativeAction.title.stringValue(),
        onClick = data.negativeAction.action,
        colors = ButtonDefaults.outlinedButtonColors(),
      )
    }
  }
}
