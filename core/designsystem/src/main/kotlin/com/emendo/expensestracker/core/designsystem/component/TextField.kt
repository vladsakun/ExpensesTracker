package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
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
  paddingValues: PaddingValues = PaddingValues(0.dp),
) {
  BasicTextField(
    value = text.orEmpty(),
    onValueChange = {
      if (it.length <= maxLength) {
        onValueChange(it)
      }
    },
    modifier = modifier,
    textStyle = textStyle,
    singleLine = singleline,
    enabled = enabled,
    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
    decorationBox = { innerTextField ->
      if (text.isNullOrBlank()) {
        Text(
          text = placeholder,
          style = PlaceholderTextStyle,
          modifier = Modifier.padding(paddingValues)
        )
      }
      Box(modifier = Modifier.padding(paddingValues)) {
        innerTextField()
      }
    }
  )
}