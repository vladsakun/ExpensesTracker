package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusShape

@Composable
fun ExpeTextFieldWithRoundedBackground(
  placeholder: String,
  text: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
    color = MaterialTheme.colorScheme.onSurface
  ),
  singleline: Boolean = true,
  enabled: Boolean = true,
  maxLength: Int = Int.MAX_VALUE,
) {
  ExpeTextField(
    placeholder = placeholder,
    text = text,
    onValueChange = onValueChange,
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerNormalRadiusShape)
      .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
      .padding(Dimens.margin_large_x),
    textStyle = textStyle,
    singleline = singleline,
    enabled = enabled,
    maxLength = maxLength,
  )
}