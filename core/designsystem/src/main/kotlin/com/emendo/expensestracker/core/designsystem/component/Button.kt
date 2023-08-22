package com.emendo.expensestracker.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusShape

@Composable
fun ExpeButton(
  @StringRes textResId: Int,
  onClick: () -> Unit,
) {
  Button(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = Dimens.margin_large_x),
    shape = RoundedCornerNormalRadiusShape,
  ) {
    Text(
      text = stringResource(id = textResId),
      modifier = Modifier.padding(Dimens.margin_small_x),
      style = MaterialTheme.typography.bodyLarge,
    )
  }
}