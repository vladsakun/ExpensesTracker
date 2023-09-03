package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit

@Composable
fun AutoSizableTextField(
  value: String,
  onValueChange: (String) -> Unit,
  minFontSize: TextUnit,
  modifier: Modifier = Modifier,
  textStyle: TextStyle = TextStyle.Default,
  maxLines: Int = Int.MAX_VALUE,
  scaleFactor: Float = 0.9f,
) {

  BoxWithConstraints(modifier = modifier) {
    val nFontSize = remember { mutableStateOf(textStyle.fontSize) }
    val calculateParagraph = @Composable {
      Paragraph(
        text = value,
        style = textStyle,
        density = LocalDensity.current,
        fontFamilyResolver = LocalFontFamilyResolver.current,
        constraints = constraints,
      )
    }

    var intrinsics = calculateParagraph()
    with(LocalDensity.current) {
      while ((intrinsics.height.toDp() > maxHeight || intrinsics.didExceedMaxLines) && nFontSize.value >= minFontSize) {
        nFontSize.value *= scaleFactor
        intrinsics = calculateParagraph()
      }
    }

    // Todo discover minIntrinsicWidth
    // Todo discover maxIntrinsicWidth
    OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      modifier = Modifier.fillMaxSize(),
      textStyle = textStyle.copy(fontSize = nFontSize.value),
      maxLines = maxLines,
    )
  }
}