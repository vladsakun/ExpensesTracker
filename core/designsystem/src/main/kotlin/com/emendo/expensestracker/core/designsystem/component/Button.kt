package com.emendo.expensestracker.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusShape

@Composable
fun ExpeButton(
  @StringRes textResId: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: ButtonColors = ButtonDefaults.buttonColors(),
  elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
  border: BorderStroke? = null,
  contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
  Button(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = Dimens.margin_large_x),
    shape = RoundedCornerNormalRadiusShape,
    enabled = enabled,
    colors = colors,
    elevation = elevation,
    border = border,
    contentPadding = contentPadding,
    interactionSource = interactionSource,
  ) {
    Text(
      text = stringResource(id = textResId),
      modifier = Modifier.padding(Dimens.margin_small_x),
      style = MaterialTheme.typography.bodyLarge,
    )
  }
}