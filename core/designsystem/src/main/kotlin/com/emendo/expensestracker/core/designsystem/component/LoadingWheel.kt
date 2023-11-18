package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.app.resources.R
import kotlinx.coroutines.launch

@Composable
fun ExpLoadingWheel(
  modifier: Modifier = Modifier,
  contentDesc: String = stringResource(id = R.string.loading),
  wheelSize: Dp = 48.dp,
) {
  val infiniteTransition = rememberInfiniteTransition(label = "Loading")

  // Specifies the float animation for slowly drawing out the lines on entering
  val startValue = if (LocalInspectionMode.current) 0F else 1F
  val floatAnimValues = (0 until NUM_OF_LINES).map { remember { Animatable(startValue) } }
  LaunchedEffect(floatAnimValues) {
    (0 until NUM_OF_LINES).map { index ->
      launch {
        floatAnimValues[index].animateTo(
          targetValue = 0F,
          animationSpec = tween(
            durationMillis = 100,
            easing = FastOutSlowInEasing,
            delayMillis = 40 * index,
          ),
        )
      }
    }
  }

  // Specifies the rotation animation of the entire Canvas composable
  val rotationAnim by infiniteTransition.animateFloat(
    initialValue = 0F,
    targetValue = 360F,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = ROTATION_TIME, easing = LinearEasing)
    ),
    label = "Rotating",
  )

  // Specifies the color animation for the base-to-progress line color change
  val baseLineColor = MaterialTheme.colorScheme.onBackground
  val progressLineColor = MaterialTheme.colorScheme.inversePrimary
  val colorAnimValues = (0 until NUM_OF_LINES).map { index ->
    infiniteTransition.animateColor(
      initialValue = baseLineColor,
      targetValue = baseLineColor,
      animationSpec = infiniteRepeatable(
        animation = keyframes {
          durationMillis = ROTATION_TIME / 2
          progressLineColor at ROTATION_TIME / NUM_OF_LINES / 2 with LinearEasing
          baseLineColor at ROTATION_TIME / NUM_OF_LINES with LinearEasing
        },
        repeatMode = RepeatMode.Restart,
        initialStartOffset = StartOffset(ROTATION_TIME / NUM_OF_LINES / 2 * index),
      ),
      label = "Color",
    )
  }

  // Draws out the LoadingWheel Canvas composable and sets the animations
  Canvas(
    modifier = modifier
      .size(wheelSize)
      .padding(8.dp)
      .graphicsLayer { rotationZ = rotationAnim }
      .semantics { contentDescription = contentDesc }
      .testTag("loadingWheel"),
  ) {
    repeat(NUM_OF_LINES) { index ->
      rotate(degrees = index * 30f) {
        drawLine(
          color = colorAnimValues[index].value,
          // Animates the initially drawn 1 pixel alpha from 0 to 1
          alpha = if (floatAnimValues[index].value < 1f) 1f else 0f,
          strokeWidth = 4F,
          cap = StrokeCap.Round,
          start = Offset(size.width / 2, size.height / 4),
          end = Offset(size.width / 2, floatAnimValues[index].value * size.height / 4),
        )
      }
    }
  }
}

@Composable
fun ExpOverlayLoadingWheel(
  contentDesc: String,
  modifier: Modifier = Modifier,
  size: Dp = 60.dp,
  wheelSize: Dp = 48.dp,
) {
  Surface(
    shape = RoundedCornerShape(60.dp),
    shadowElevation = 8.dp,
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.83f),
    modifier = modifier
      .size(size)
  ) {
    ExpLoadingWheel(
      contentDesc = contentDesc,
      wheelSize = wheelSize,
    )
  }
}

@ThemePreviews
@Composable
fun ExpLoadingWheelPreview() {
  MaterialTheme {
    Surface {
      ExpLoadingWheel(contentDesc = "Loading Wheel")
    }
  }
}

@ThemePreviews
@Composable
fun ExpOverlayLoadingWheelPreview() {
  MaterialTheme {
    Surface {
      ExpOverlayLoadingWheel(contentDesc = "Loading Wheel")
    }
  }
}

private const val ROTATION_TIME = 12000
private const val NUM_OF_LINES = 12