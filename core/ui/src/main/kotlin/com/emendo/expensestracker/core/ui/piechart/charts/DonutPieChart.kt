package com.emendo.expensestracker.core.ui.piechart.charts

import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.text.TextUtils
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.graphics.withRotation
import com.emendo.expensestracker.core.ui.piechart.PieChartConstants.NO_SELECTED_SLICE
import com.emendo.expensestracker.core.ui.piechart.models.PieChartConfig
import com.emendo.expensestracker.core.ui.piechart.models.PieChartData
import com.emendo.expensestracker.core.ui.piechart.utils.convertTouchEventPointToAngle
import com.emendo.expensestracker.core.ui.piechart.utils.getSliceCenterPoints
import com.emendo.expensestracker.core.ui.piechart.utils.proportion
import com.emendo.expensestracker.core.ui.piechart.utils.sweepAngles
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

/**
 * Compose function for Drawing Donut chart
 * @param modifier : All modifier related property
 * @param pieChartData: data list for the pie chart
 * @param onSliceClick(pieChartData.Slice)->Unit: The event that captures the click
 */
@Composable
fun DonutPieChart(
  pieChartData: PieChartData,
  pieChartConfig: PieChartConfig,
  modifier: Modifier = Modifier,
  onSliceClick: (PieChartData.Slice) -> Unit = {},
) {
  if (pieChartData.slices.isEmpty()) {
    // Handle empty data
    return
  }

  var animationPlayed by rememberSaveable { mutableStateOf(false) }

  // Sum of all the values
  val sumOfValues: Float = pieChartData.totalLength

  // Calculate each proportion value
  val proportions: List<Float> = pieChartData.slices.proportion(sumOfValues)

  // Convert each proportions to angle
  val sweepAngles: List<Float> = proportions.sweepAngles()

  val progressSize: MutableList<Float> = mutableListOf(sweepAngles.first())

  for (x in 1 until sweepAngles.size) {
    progressSize.add(sweepAngles[x] + progressSize[x - 1])
  }

  var activePie by rememberSaveable { mutableIntStateOf(NO_SELECTED_SLICE) }
  BoxWithConstraints(modifier = modifier) {
    val sideSize: Int = Integer.min(constraints.maxWidth, constraints.maxHeight)

    // Padding in percentage
    val padding = (sideSize * pieChartConfig.chartPadding) / 100f

    val size = Size(sideSize.toFloat() - padding, sideSize.toFloat() - padding)

    val pathPortion = remember { Animatable(initialValue = 0f) }

    val animatablesSize: List<Animatable<Float, AnimationVector1D>> = sweepAngles.map { Animatable(0f) }
    val animatablesAngleSize: List<Animatable<Float, AnimationVector1D>> = sweepAngles.map { Animatable(0f) }

    if (pieChartConfig.isAnimationEnable) {
      LaunchedEffect(key1 = Unit) {
        pathPortion.animateTo(
          targetValue = 1f,
          animationSpec = tween(pieChartConfig.animationDuration),
        )
        animationPlayed = true
      }
    }
    val coroutineScope = rememberCoroutineScope()
    val surface = MaterialTheme.colorScheme.surface
    Canvas(
      modifier = Modifier
        .width(sideSize.dp)
        .height(sideSize.dp)
        .pointerInput(Unit) {
          detectTapGestures {
            val clickedAngle =
              convertTouchEventPointToAngle(
                width = sideSize.toFloat(),
                height = sideSize.toFloat(),
                xPos = it.x,
                yPos = it.y,
              )
            progressSize.forEachIndexed { index, item ->
              if (clickedAngle <= item) {
                activePie =
                  if (activePie != index) {
                    index
                  } else {
                    NO_SELECTED_SLICE
                  }
                onSliceClick(pieChartData.slices[index])
                return@detectTapGestures
              }
            }
          }
        },
    ) {
      var sAngle = pieChartConfig.startAngle
      val sliceLabelPaint =
        TextPaint().apply {
          isAntiAlias = true
          textSize = pieChartConfig.sliceLabelTextSize.toPx()
          textAlign = Paint.Align.CENTER
          color = pieChartConfig.sliceLabelTextColor.toArgb()
          typeface = pieChartConfig.sliceLabelTypeface
        }

      sweepAngles.forEachIndexed { index, arcProgress ->
        val isActive = activePie == index

        coroutineScope.launch {
          launch {
            if (isActive) {
              animatablesSize[index].animateTo(100f, tween(300, easing = LinearOutSlowInEasing))
            } else {
              animatablesSize[index].animateTo(0f, tween(200, easing = LinearOutSlowInEasing))
            }
          }

          launch {
            if (isActive) {
              animatablesAngleSize[index].animateTo(10f, tween(300, easing = LinearOutSlowInEasing))
            } else {
              animatablesAngleSize[index].animateTo(0f, tween(200, easing = LinearOutSlowInEasing))
            }
          }
        }

        val arcProgressAnimated =
          if (pieChartConfig.isAnimationEnable && !animationPlayed) {
            arcProgress * pathPortion.value
          } else {
            arcProgress
          }
        drawPie(
          color = pieChartData.slices[index].color,
          startAngle = sAngle + animatablesAngleSize[index].value,
          arcProgress = arcProgressAnimated - animatablesAngleSize[index].value * 2,
          size = size,
          padding = padding,
          strokeWidth = pieChartConfig.strokeWidth + animatablesSize[index].value,
          isActive = isActive,
          pieChartConfig = pieChartConfig,
        )

        val (x, y) = getSliceCenterPoints(
          sAngle = sAngle,
          arcProgress = arcProgress,
          size = size,
          padding = padding,
          sizeChange = animatablesSize[index].value,
        )

        // Todo handle corner case when after rounding the sum of all proportions is not equal to 100
        val number = proportions[index]

        if (number >= 5) {
          val rounded = BigDecimal(number.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toFloat()
          val label = "$rounded%"

          // find the height of text
          val height = label.getTextHeight(sliceLabelPaint)

          val ellipsizedText by lazy {
            TextUtils.ellipsize(
              label,
              sliceLabelPaint,
              pieChartConfig.sliceMinTextWidthToEllipsize.toPx(),
              pieChartConfig.sliceLabelEllipsizeAt,
            ).toString()
          }

          drawIntoCanvas {
            it.nativeCanvas.withRotation(0f, x, y) {
              it.nativeCanvas.drawText(
                if (pieChartConfig.isEllipsizeEnabled) ellipsizedText else label,
                x,
                y + abs(height) / 2,
                sliceLabelPaint,
              )
            }
          }
        }

        sAngle += arcProgress
      }

      // Center circle to hide the part of the arc during the animation that goes into the center
      drawCircle(
        color = surface,
        radius = (size.width - pieChartConfig.strokeWidth) / 2,
        center = Offset(x = size.center.x + padding / 2, y = size.center.y + padding / 2),
      )
    }
  }
}

/**
 * return the height of text in canvas drawn text
 */
fun String.getTextHeight(paint: Paint): Int {
  val bounds = Rect()
  paint.getTextBounds(
    this,
    0,
    this.length,
    bounds,
  )
  return bounds.height()
}
