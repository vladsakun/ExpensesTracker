package com.emendo.expensestracker.core.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusShape
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color

@Composable
inline fun SelectRow(
  @StringRes labelResId: Int,
  noinline onClick: () -> Unit,
  labelModifier: @Composable RowScope.() -> Modifier = { Modifier },
  endLayout: @Composable RowScope.() -> Unit = {},
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = Dimens.min_select_row_height)
      .clip(RoundedCornerNormalRadiusShape)
      .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
      .clickable(onClick = onClick)
      .padding(Dimens.margin_large_x),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = stringResource(id = labelResId),
      style = MaterialTheme.typography.bodyLarge,
      modifier = labelModifier(),
    )
    endLayout()
  }
}

@Composable
inline fun SelectRowWithIcon(
  @StringRes labelResId: Int,
  imageVectorProvider: () -> ImageVector,
  noinline onClick: () -> Unit,
) {
  val labelModifier: @Composable RowScope.() -> Modifier = remember { { Modifier.weight(1f) } }
  SelectRow(
    labelResId = labelResId,
    onClick = onClick,
    labelModifier = labelModifier,
    endLayout = {
      Icon(
        imageVector = imageVectorProvider(),
        contentDescription = "label",
      )
    },
  )
}

// Todo make work without recomposition
@Composable
fun SelectRowWithText(
  @StringRes labelResId: Int,
  textProvider: () -> String,
  onClick: () -> Unit,
  textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
  val labelModifier: @Composable RowScope.() -> Modifier = remember { { Modifier.weight(1f) } }
  val text = textProvider()
  SelectRow(
    labelResId = labelResId,
    onClick = onClick,
    labelModifier = labelModifier,
    endLayout = {
      Text(
        text = "text",
        style = textStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.End,
      )
    }
  )
}

@Composable
inline fun SelectRowWithColor(
  @StringRes labelResId: Int,
  colorProvider: () -> ColorModel,
  noinline onClick: () -> Unit,
) {
  SelectRow(
    labelResId = labelResId,
    onClick = onClick,
    endLayout = {
      Box(
        modifier = Modifier
          .size(Dimens.icon_size)
          .aspectRatio(1f)
          .clip(shape = CircleShape)
          .background(color = colorProvider().color)
      )
    },
  )
}