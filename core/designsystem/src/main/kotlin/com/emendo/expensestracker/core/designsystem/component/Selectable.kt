package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusShape

@Composable
fun SelectRow(
  label: String,
  onClick: () -> Unit,
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
      text = label,
      style = MaterialTheme.typography.bodyLarge,
      modifier = labelModifier(),
    )
    endLayout()
  }
}

@Composable
fun SelectRowWithIcon(
  label: String,
  imageVector: ImageVector,
  onClick: () -> Unit,
) {
  SelectRow(
    label = label,
    onClick = onClick,
    labelModifier = { Modifier.weight(1f) },
    endLayout = {
      Icon(
        imageVector = imageVector,
        contentDescription = "label",
      )
    },
  )
}

@Composable
fun SelectRowWithText(
  label: String,
  text: String,
  onClick: () -> Unit,
  textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
  SelectRow(
    label = label,
    onClick = onClick,
    labelModifier = { Modifier.weight(1f) },
    endLayout = {
      Text(
        text = text,
        style = textStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.End,
      )
    },
  )
}