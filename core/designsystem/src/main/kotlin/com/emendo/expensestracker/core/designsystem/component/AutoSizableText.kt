package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import timber.log.Timber

@Composable
fun AutoSizableText(
  text: String,
  minFontSize: TextUnit,
  textAlign: TextAlign,
  modifier: Modifier = Modifier,
  textStyle: TextStyle = TextStyle.Default,
  maxLines: Int = Int.MAX_VALUE,
  scaleFactor: Float = 0.9f,
) {
  BoxWithConstraints(modifier = modifier) {
    Timber.d("maxHeight: $maxHeight")
    var nFontSize = textStyle.fontSize
    val calculateParagraph = @Composable {
      Paragraph(
        text = text,
        style = textStyle.copy(fontSize = nFontSize),
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

    // Todo discover minIntrinsicWidth, maxIntrinsicWidth
    Text(
      text = text,
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter),
      style = textStyle.copy(fontSize = nFontSize),
      maxLines = maxLines,
      textAlign = textAlign,
    )
  }
}