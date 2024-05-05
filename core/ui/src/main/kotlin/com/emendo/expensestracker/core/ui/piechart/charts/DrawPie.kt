package com.emendo.expensestracker.core.ui.piechart.charts

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.emendo.expensestracker.core.ui.piechart.models.PieChartConfig

/**
 * Extension for drawing arcs
 * @param color : Color for slice
 * @param startAngle : StartAngle for the slice, from where to start draw
 * @param arcProgress : Process of slice
 * @param size : Size of the chart
 * @param strokeWidth : StrokeWidth for the pie chart
 * @param padding : Padding from top left
 * @param isActive : DonutPieChart zoom slice if IsActive
 */

fun DrawScope.drawPie(
  color: Color,
  startAngle: Float,
  arcProgress: Float,
  size: Size,
  strokeWidth: Float = 100f,
  padding: Float,
  isActive: Boolean = false,
  pieChartConfig: PieChartConfig,
) {
  drawArc(
    color = color,
    startAngle = startAngle,
    sweepAngle = arcProgress,
    useCenter = false,
    size = size,
    style = Stroke(width = strokeWidth),
    topLeft = Offset(x = padding / 2, y = padding / 2),
  )
}
