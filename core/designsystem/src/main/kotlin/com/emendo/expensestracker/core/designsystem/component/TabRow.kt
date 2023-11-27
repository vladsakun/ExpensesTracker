package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import kotlinx.collections.immutable.ImmutableList

fun ContentDrawScope.drawWithLayer(block: ContentDrawScope.() -> Unit) {
  with(drawContext.canvas.nativeCanvas) {
    val checkPoint = saveLayer(null, null)
    block()
    restoreToCount(checkPoint)
  }
}

// Todo discover
@Composable
fun TextSwitch(
  selectedIndex: Int,
  items: ImmutableList<String>,
  onSelectionChange: (Int) -> Unit,
  modifier: Modifier = Modifier,
  selectedTextColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
  cornerRadius: Dp = Dimens.corner_radius_normal,
  backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
  indicatorColor: Color = MaterialTheme.colorScheme.primary,
) {
  require(items.isNotEmpty()) { "Items can't be empty" }
  BoxWithConstraints(
    modifier
      .padding(4.dp)
      .height(40.dp)
      .clip(RoundedCornerShape(cornerRadius))
      .background(backgroundColor)
      .padding(4.dp)
  ) {
    val maxWidth = this.maxWidth
    val tabWidth = maxWidth / items.size

    val indicatorOffset by animateDpAsState(
      targetValue = tabWidth * selectedIndex,
      animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
      label = "indicator offset"
    )

    // This is for shadow layer matching white background
    Box(
      modifier = Modifier
        .offset(x = indicatorOffset)
        // Todo play with shadow color
        .shadow(4.dp, RoundedCornerShape(cornerRadius))
        .width(tabWidth)
        .fillMaxHeight()
    )

    Row(modifier = Modifier
      .fillMaxWidth()
      .drawWithContent {
        // This is for setting black tex while drawing on white background
        val padding = 4.dp.toPx()
        drawRoundRect(
          topLeft = Offset(x = indicatorOffset.toPx() + padding, padding),
          size = Size(size.width / items.size - padding * 2, size.height - padding * 2),
          color = selectedTextColor,
          cornerRadius = CornerRadius(x = cornerRadius.toPx(), y = cornerRadius.toPx()),
        )

        val brush = Brush.Companion.horizontalGradient(listOf(Color.Red))

        drawWithLayer {
          drawContent()
          // This is white top rounded rectangle
          drawRoundRect(
            topLeft = Offset(x = indicatorOffset.toPx(), 0f),
            size = Size(size.width / items.size, size.height),
            color = indicatorColor,
            cornerRadius = CornerRadius(x = cornerRadius.toPx(), y = cornerRadius.toPx()),
            blendMode = BlendMode.SrcOut,
          )
        }

      }
    ) {
      items.forEachIndexed { index, text ->
        Box(
          modifier = Modifier
            .width(tabWidth)
            .fillMaxHeight()
            .clickable(
              interactionSource = remember {
                MutableInteractionSource()
              },
              indication = null,
              onClick = {
                onSelectionChange(index)
              }
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
          )
        }
      }
    }
  }
}