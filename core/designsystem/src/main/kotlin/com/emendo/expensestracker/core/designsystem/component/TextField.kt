package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.designsystem.theme.Dimens
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
  startIcon: (@Composable (() -> Unit))? = null,
  endIcon: (@Composable (() -> Unit))? = null,
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
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(paddingValues),
      ) {
        if (startIcon != null) {
          startIcon()
          Spacer(modifier = Modifier.width(Dimens.margin_small_x))
        }
        Box(modifier = Modifier.weight(1f)) {
          if (text.isNullOrBlank()) {
            Text(
              text = placeholder,
              style = PlaceholderTextStyle,
            )
          }
          innerTextField()
        }
        if (endIcon != null) {
          Spacer(modifier = Modifier.width(Dimens.margin_small_x))
          endIcon()
        }
      }
    }
  )
}

@Composable
fun ExpeTextFieldClearIcon(text: String?, onValueChange: (String) -> Unit) {
  if (!text.isNullOrEmpty()) {
    IconButton(onClick = { onValueChange("") }) {
      Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "Clear text",
      )
    }
  }
}