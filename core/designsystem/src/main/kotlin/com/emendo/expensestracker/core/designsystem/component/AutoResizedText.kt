package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isUnspecified
import timber.log.Timber

@Composable
fun AutoResizedText(
  text: String,
  minFontSize: TextUnit,
  modifier: Modifier = Modifier,
  textAlign: TextAlign = TextAlign.Start,
  maxLines: Int = Int.MAX_VALUE,
  style: TextStyle = LocalTextStyle.current,
  color: Color = style.color,
  overflow: TextOverflow = TextOverflow.Clip,
) {
  var resizedTextStyle by remember { mutableStateOf(style) }
  var shouldDraw by remember { mutableStateOf(false) }

  val defaultFontSize = MaterialTheme.typography.bodyMedium.fontSize

  Text(
    text = text,
    color = color,
    modifier = modifier.drawWithContent {
      if (shouldDraw) {
        drawContent()
      }
    },
    textAlign = textAlign,
    maxLines = maxLines,
    softWrap = false,
    overflow = overflow,
    style = resizedTextStyle,
    onTextLayout = { result ->
      if (result.didOverflowWidth) {
        if (style.fontSize.isUnspecified) {
          resizedTextStyle = resizedTextStyle.copy(
            fontSize = defaultFontSize
          )
        }
        val decreasedFontSize = resizedTextStyle.fontSize * 0.95
        if (decreasedFontSize <= minFontSize) {
          return@Text
        }

        resizedTextStyle = resizedTextStyle.copy(fontSize = decreasedFontSize)
      } else {
        shouldDraw = true
      }
    }
  )
}