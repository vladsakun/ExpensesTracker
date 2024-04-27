package com.emendo.expensestracker.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.designsystem.component.ThemePreviews
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color

@Composable
fun AccountItem(
  color: Color,
  icon: ImageVector,
  name: String,
  balance: String,
  selectedProvider: () -> Boolean,
  draggableStateProvider: () -> Boolean,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .heightIn(min = Dimens.icon_button_size)
      .padding(
        horizontal = Dimens.margin_large_x,
        vertical = Dimens.margin_large_x,
      ),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x)
  ) {
    Box {
      Icon(
        tint = color,
        imageVector = icon,
        contentDescription = "",
        modifier = Modifier
          .size(Dimens.icon_size_large)
      )
      AnimatedContent(
        selectedProvider(),
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .size(Dimens.icon_size)
          .offset(x = Dimens.margin_small_x),
        label = "selected"
      ) { selected ->
        if (selected) {
          Icon(
            tint = MaterialTheme.customColorsPalette.successColor,
            imageVector = ExpeIcons.CheckCircle,
            contentDescription = "",
            modifier = Modifier
              .clip(RoundedCornerShape(100))
              .background(MaterialTheme.colorScheme.surface)
          )
        }
      }
    }
    Text(
      text = name,
      modifier = Modifier
        .weight(1f),
      style = MaterialTheme.typography.bodyMedium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    AnimatedContent(
      draggableStateProvider(),
      label = "dragState",
    ) { isDraggable ->
      if (isDraggable) {
        Icon(
          imageVector = ExpeIcons.DragHandle,
          contentDescription = "Drag Handle",
        )
      } else {
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
  }
}

@ThemePreviews
@Composable
private fun AccountItemPreview() {
  ExpensesTrackerTheme {
    Surface {
      Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
        modifier = Modifier.padding(horizontal = Dimens.margin_large_x)
      ) {
        Text("Initial state")
        AccountItem(
          color = ColorModel.Purple.color,
          icon = IconModel.CREDITCARD.imageVector,
          name = "Card",
          balance = "$ 1000.00",
          selectedProvider = { false },
          draggableStateProvider = { false },
        )
        Text("Selected state")
        AccountItem(
          color = ColorModel.Purple.color,
          icon = IconModel.CREDITCARD.imageVector,
          name = "Card",
          balance = "$ 1000.00",
          selectedProvider = { true },
          draggableStateProvider = { true },
        )
      }
    }
  }
}