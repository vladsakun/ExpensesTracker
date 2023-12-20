package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.designsystem.utils.pxToDp
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@Composable
fun AutoSizableTextField(
  text: String,
  minFontSize: TextUnit,
  focused: Boolean,
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
        text = text,
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

    // Todo discover minIntrinsicWidth, maxIntrinsicWidth
    Text(
      text = text,
      modifier = Modifier
        .fillMaxWidth()
        .padding(end = 2.dp)
        .align(Alignment.BottomCenter),
      style = style.copy(fontSize = nFontSize),
      maxLines = maxLines,
      textAlign = textAlign,
      color = color,
      overflow = overflow,
    )
    if (focused) {
      Spacer(
        modifier = Modifier
          .width(DefaultCursorThickness)
          .height(
            intrinsics.height
              .roundToInt()
              .pxToDp()
          )
          .cursor(
            cursorBrush = SolidColor(value = MaterialTheme.colorScheme.primary),
          )
          .align(Alignment.CenterEnd),
      )
    }
  }
}

internal fun Modifier.cursor(
  cursorBrush: Brush,
) = composed {
  val cursorAlpha = remember { Animatable(1f) }
  LaunchedEffect(Unit) {
    // Animate the cursor even when animations are disabled by the system.
    withContext(FixedMotionDurationScale) {
      // ensure that the value is always 1f _this_ frame by calling snapTo
      cursorAlpha.snapTo(1f)
      // then start the cursor blinking on animation clock (500ms on to start)
      cursorAlpha.animateTo(0f, cursorAnimationSpec)
    }
  }
  drawWithContent {
    val cursorAlphaValue = cursorAlpha.value.coerceIn(0f, 1f)
    if (cursorAlphaValue != 0f) {
      val cursorRect = Rect(0f, 0f, 0f, size.height)
      val cursorWidth = DefaultCursorThickness.toPx()
      val cursorX = (cursorRect.left + cursorWidth / 2)
        .coerceAtMost(size.width - cursorWidth / 2)

      drawLine(
        brush = cursorBrush,
        start = Offset(cursorX, cursorRect.top),
        end = Offset(cursorX, cursorRect.bottom),
        alpha = cursorAlphaValue,
        strokeWidth = cursorWidth
      )
    }
  }
}

private val cursorAnimationSpec: AnimationSpec<Float> = infiniteRepeatable(
  animation = keyframes {
    durationMillis = 1000
    1f at 0
    1f at 499
    0f at 500
    0f at 999
  }
)

internal val DefaultCursorThickness = 2.dp

private object FixedMotionDurationScale : MotionDurationScale {
  override val scaleFactor: Float
    get() = 1f
}