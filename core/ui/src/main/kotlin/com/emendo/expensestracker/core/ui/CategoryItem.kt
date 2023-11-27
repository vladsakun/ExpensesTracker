package com.emendo.expensestracker.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.emendo.expensestracker.core.app.resources.icon.AccountIcons
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusShape

@Composable
fun CategoryItem(
  name: String,
  color: Color,
  icon: ImageVector,
  total: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .clip(RoundedCornerNormalRadiusShape)
      .background(color)
      .clickable(onClick = onClick)
      .padding(Dimens.margin_small_x),
    verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      imageVector = icon,
      contentDescription = "$name category icon",
      tint = Color.White, // Todo remove hardcoded color
    )
    Text(
      text = name,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = MaterialTheme.typography.labelSmall,
      color = Color.White,
    )
    Text(
      text = total,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = MaterialTheme.typography.labelSmall,
      color = Color.White,
    )
  }
}

@Composable
fun AddCategoryItem(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  // Todo do custom ripple effect animation
  Box(modifier = Modifier.height(IntrinsicSize.Max)) {
    CategoryItem(
      name = "Childcare",
      color = Color.White,
      icon = AccountIcons.ChildCare,
      total = "$100",
      onClick = { },
      modifier = Modifier.alpha(0f)
    )
    Icon(
      imageVector = ExpeIcons.Add,
      contentDescription = "icon",
      modifier = modifier
        .fillMaxSize()
        .clip(RoundedCornerNormalRadiusShape)
        .clickable(onClick = onClick)
        .background(color = MaterialTheme.colorScheme.primaryContainer)
        .padding(Dimens.margin_large_x),
    )
  }
}