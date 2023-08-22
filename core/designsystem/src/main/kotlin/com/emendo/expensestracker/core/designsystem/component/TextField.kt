package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.PlaceholderTextStyle
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusShape
import com.emendo.expensestracker.core.designsystem.utils.TextFieldState

@Composable
fun ExpeTextField(
  label: String,
  textFieldState: TextFieldState,
  text: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  singleline: Boolean = true,
  enabled: Boolean = true,
) {
  BasicTextField(
    value = text,
    onValueChange = onValueChange,
    textStyle = MaterialTheme.typography.bodyLarge.copy(
      color = MaterialTheme.colorScheme.onSurface
    ),
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerNormalRadiusShape)
      .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
      .padding(Dimens.margin_large_x),
    singleLine = singleline,
    enabled = enabled,
    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
    decorationBox = { innerTextField ->
      if (text.isEmpty()) {
        Text(
          text = label,
          style = PlaceholderTextStyle,
        )
      }
      innerTextField()
    }
  )
}