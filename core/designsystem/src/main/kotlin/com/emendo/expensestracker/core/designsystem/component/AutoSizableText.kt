package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

@Composable
fun AutoSizableText(
  textProvider: () -> String,
  minFontSize: TextUnit,
  modifier: Modifier = Modifier,
  textAlign: TextAlign? = null,
  style: TextStyle = TextStyle.Default,
  maxLines: Int = Int.MAX_VALUE,
  scaleFactor: Float = 0.9f,
  color: Color = Color.Unspecified,
  overflow: TextOverflow = TextOverflow.Clip,
) {
  BoxWithConstraints(modifier = modifier) {
    var nFontSize = style.fontSize
    val calculateParagraph = @Composable {
      Paragraph(
        text = textProvider(),
        style = style.copy(fontSize = nFontSize),
        density = LocalDensity.current,
        fontFamilyResolver = LocalFontFamilyResolver.current,
        constraints = constraints.copy(minWidth = 0, minHeight = 0),
        maxLines = maxLines,
      )
    }

    var intrinsics = calculateParagraph()
    with(LocalDensity.current) {
      val heightInDp = intrinsics.height.toDp()
      while ((heightInDp > maxHeight || intrinsics.didExceedMaxLines) && nFontSize >= minFontSize) {
        nFontSize *= scaleFactor
        intrinsics = calculateParagraph()
      }
    }

    Text(
      text = textProvider(),
      modifier = Modifier
        .align(Alignment.BottomCenter),
      style = style.copy(fontSize = nFontSize),
      maxLines = maxLines,
      textAlign = textAlign,
      color = color,
      overflow = overflow,
    )
  }
}