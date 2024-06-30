package com.emendo.expensestracker.core.ui.piechart.models

import androidx.compose.ui.graphics.Color
import com.emendo.expensestracker.core.ui.piechart.models.PieChartData.Slice
import com.emendo.expensestracker.core.ui.piechart.utils.sum
import kotlinx.collections.immutable.ImmutableList

/**
 * PieChartData is a data class to mention all the data needed to draw slices in the pie/donut chart.
 * @param slices: Defines the list of slices [Slice] to be drawn.
 */
data class PieChartData(val slices: ImmutableList<Slice>) {
    val totalLength: Float
        get() = slices.sum()

    /**
     * Slice is data class to mention data of each arc in a 360 degree chart.
     * @param value: Value of the arc.
     * @param color: Color of the arc.
     */
    data class Slice(
        val value: Float,
        val color: Color,
    )
}
