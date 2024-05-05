package com.emendo.expensestracker.core.ui.piechart.models

import androidx.compose.ui.graphics.Color
import com.emendo.expensestracker.core.ui.piechart.models.PieChartData.Slice
import com.emendo.expensestracker.core.ui.piechart.utils.sum

/**
 * PieChartData is a data class to mention all the data needed to draw slices in the pie/donut chart.
 * @param slices: Defines the list of slices [Slice] to be drawn.
 * @param plotType: Defines the type of the chart.
 */
data class PieChartData(val slices: List<Slice>) {
  val totalLength: Float
    get() {
      return slices.sum()
    }

  /**
   * Slice is data class to mention data of each arc in a 360 degree chart.
   * @param label: Name of the arc.
   * @param value: Value of the arc.
   * @param color: Color of the arc.
   * @param sliceDescription: Description of the arc for accessibility service.
   */
  data class Slice(
    val label: String,
    val value: Float,
    val color: Color,
    val sliceDescription: (Int) -> String = { slicePercentage ->
      "Slice name : $label  \nPercentage  : $slicePercentage %"
    },
  )
}
