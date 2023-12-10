package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import com.emendo.expensestracker.core.designsystem.theme.PlaceholderTextStyle

@Composable
fun ExpeTextField(
  placeholder: String,
  text: String?,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
    color = MaterialTheme.colorScheme.onSurface
  ),
  singleline: Boolean = true,
  enabled: Boolean = true,
  maxLength: Int = Int.MAX_VALUE,
) {
  BasicTextField(
    value = text.orEmpty(),
    onValueChange = {
      if (it.length <= maxLength) {
        onValueChange(it)
      }
    },
    textStyle = textStyle,
    modifier = modifier,
    singleLine = singleline,
    enabled = enabled,
    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
    decorationBox = { innerTextField ->
      if (text.isNullOrBlank()) {
        Text(
          text = placeholder,
          style = PlaceholderTextStyle,
        )
      }
      innerTextField()
    }
  )
}