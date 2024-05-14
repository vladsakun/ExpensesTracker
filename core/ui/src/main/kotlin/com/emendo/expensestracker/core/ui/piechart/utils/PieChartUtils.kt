package com.emendo.expensestracker.core.ui.piechart.utils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import com.emendo.expensestracker.core.ui.piechart.PieChartConstants.ONE_HUNDRED
import com.emendo.expensestracker.core.ui.piechart.PieChartConstants.TOTAL_ANGLE
import com.emendo.expensestracker.core.ui.piechart.models.PieChartData
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

const val TAG = "PieChartUtils"

/**
 * Returns the angle for given touch point
 * @param width: Width of the chart.
 * @param height: Height of the chart.
 * @param xPos: X offset of the tap point.
 * @param yPos: Y offset of the tap point.
 */
fun convertTouchEventPointToAngle(
    width: Float,
    height: Float,
    xPos: Float,
    yPos: Float,
): Double {
    /**
     * We are transforming the touch event coordinates
     * from the screen's coordinate system to the chart's coordinate system.
     * This is a common technique in computer graphics and game programming.
     */

    // X coordinate of touch event relative to the chart's center
    val x = xPos - (width * 0.5f)
    // Y coordinate of touch event relative to the chart's center
    val y = yPos - (height * 0.5f)

    /**
     * Converting the touch event coordinates to an angle (from Cartesian coordinates into Polar coordinates)
     * Adding Math.PI / 2 rotates the angle by 90 degrees, so that 0 degrees is at the top of the chart.
     */
    var angle = Math.toDegrees(atan2(y = y.toDouble(), x = x.toDouble()) + Math.PI / 2)

    /**
     * This line ensures that the angle is always between 0 and 360 degrees.
     * The atan2() function returns an angle between -π and π radians, which corresponds to -180 to 180 degrees.
     * If the angle is negative, adding 360 degrees makes it positive.
     */
    angle = if (angle < 0) angle + 360 else angle
    return angle
}

/**
 * Returns the sum of all the arc values
 */
fun List<PieChartData.Slice>.sum(): Float {
    return this.map { it.value }.sum()
}

/**
 * Returns the center points of the slice
 * @param sAngle Start angle of the point
 * @param arcProgress Progress angle of the point
 * @param size Size of the canvas
 * @param padding padding of the canvas
 */
fun getSliceCenterPoints(
    sAngle: Float,
    arcProgress: Float,
    size: Size,
    padding: Float,
    sizeChange: Float,
): Pair<Float, Float> {
    // Center angle of the slice (arc). Start angle + half of the progress angle
    val arcCenter = sAngle + (arcProgress / 2)
    // Middle point radius is half of the radius of the pie chart
    val pointRadius = size.width / 2 + sizeChange / 3

  /*
   * Calculate the x & y co-ordinates to show the label/percentage tex
   * find points using angle and radius
   * https://en.wikipedia.org/wiki/Polar_coordinate_system#Converting_between_polar_and_Cartesian_coordinates
   *
   * x = radius * cos(angle)
   * y = radius * sin(angle)
   *
   * The resulting x and y coordinates are relative to the center of the pie chart,
   * so size.center.x + padding / 2 and size.center.y + padding / 2
   * are added to shift them to the pie chart's coordinate system.
   *
   * We should add size.center.x and size.center.y to coordinates respectively,
   * because by formula to convert polar to cartesian coordinate, the center of the circle is (0,0),
   * but in Canvas (0,0) is top left corner.
   *
   * Therefore, to correctly position the center point of the slice in the canvas,
   * we need to shift the x and y coordinates by adding
   * the x and y coordinates of the center of the pie chart (size.center.x and size.center.y).
   *
   * The padding / 2 is added to account for any padding that might be present around the pie chart.
   * This ensures that the center point of the slice is correctly positioned
   * even if there is padding around the pie chart.
   */
    val x =
        (pointRadius * cos(Math.toRadians(arcCenter.toDouble()))) +
            size.center.x + padding / 2
    val y =
        (pointRadius * sin(Math.toRadians(arcCenter.toDouble()))) +
            size.center.y + padding / 2

    return Pair(x.toFloat(), y.toFloat())
}

/**
 * Returns the calculated proportion value of each arc
 * @param total: Total of the the slices.
 */
fun List<PieChartData.Slice>.proportion(total: Float): List<Float> {
    return this.map {
        it.value * ONE_HUNDRED / total
    }
}

/**
 * Returns the list of sweep angles
 */
fun List<Float>.sweepAngles(): List<Float> {
    return this.map {
        TOTAL_ANGLE * it / ONE_HUNDRED
    }
}
