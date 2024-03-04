package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.emendo.expensestracker.core.designsystem.theme.Dimens

@Composable
fun Chevron(
  width: Dp,
  height: Dp,
  modifier: Modifier = Modifier,
) {
  val color = DividerDefaults.color
  Spacer(
    modifier = modifier
      .width(width)
      .height(height)
      .drawWithCache {
        val path = Path()
        path.lineTo(size.width, size.height / 2f)
        path.lineTo(0f, size.height)
        onDrawBehind {
          drawPath(
            path = path,
            color = color,
            style = Stroke(width = Dimens.divider_thickness.toPx(), cap = StrokeCap.Round),
          )
        }
      }
  )
}