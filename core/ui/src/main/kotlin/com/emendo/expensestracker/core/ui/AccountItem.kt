package com.emendo.expensestracker.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.ColorModel.Companion.color
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.designsystem.component.ThemePreviews
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme

@Composable
fun AccountItem(
  color: Color,
  icon: ImageVector,
  name: String,
  balance: String,
  onClick: () -> Unit = {},
) {
  Row(
    modifier = Modifier
      .heightIn(min = Dimens.icon_button_size)
      .clickable(onClick = onClick)
      .padding(
        horizontal = Dimens.margin_large_x,
        vertical = Dimens.margin_small_x
      ),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(Dimens.margin_small_x)
  ) {
    Icon(
      //        modifier = Modifier
      //          .clip(RoundedCornerShape(Dimens.corner_radius_small))
      //          .background(color = color.copy(alpha = 0.2f))
      //          .border(
      //            width = Dimens.border_thickness,
      //            color = color,
      //            shape = RoundedCornerShape(Dimens.corner_radius_small)
      //          )
      //          .padding(Dimens.margin_small_x),
      tint = color,
      imageVector = icon,
      contentDescription = "",
    )
    Text(
      text = name,
      modifier = Modifier
        .weight(1f),
      style = MaterialTheme.typography.bodyMedium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    Text(
      modifier = Modifier
        .weight(2f),
      text = balance,
      style = MaterialTheme.typography.bodyMedium,
      textAlign = TextAlign.End,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
    )
  }
}

@ThemePreviews
@Composable
private fun AccountItemPreview() {
  ExpensesTrackerTheme {
    AccountItem(
      color = ColorModel.Purple.color,
      icon = IconModel.CREDITCARD.imageVector,
      name = "Card",
      balance = "$ 1000.00",
      onClick = {},
    )
  }
}