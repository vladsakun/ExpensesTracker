package com.emendo.expensestracker.core.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.emendo.expensestracker.core.app.resources.icon.AccountIcons
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.AutoResizedText
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusShape
import com.emendo.expensestracker.core.designsystem.utils.toDp

private val ItemPadding
  get() = Dimens.margin_small_x

@Composable
fun CategoryItem(
  name: String,
  color: Color,
  icon: ImageVector,
  total: String,
  onClick: () -> Unit,
  onDeleteClick: () -> Unit,
  isEditMode: () -> Boolean,
  modifier: Modifier = Modifier,
) {
  val infiniteTransition = rememberInfiniteTransition(label = "rotationTransition")
  val transition = infiniteTransition.animateFloat(
    initialValue = -4f,
    targetValue = 4f,
    animationSpec = infiniteRepeatable(
      animation = tween(200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "rotation"
  )

  Box {
    if (isEditMode()) {
      IconButton(
        modifier = Modifier
          .zIndex(2f)
          .graphicsLayer {
            val translation = -20f
            translationX = translation
            translationY = translation
          },
        onClick = onDeleteClick
      ) {
        Icon(
          imageVector = ExpeIcons.Remove,
          contentDescription = "remove",
          modifier = Modifier
            .size(Dimens.icon_size)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
        )
      }
    }
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(ItemPadding)
        .graphicsLayer {
          if (isEditMode()) {
            rotationZ = transition.value
            translationX = transition.value
          }
        }
        .clip(RoundedCornerNormalRadiusShape)
        .then(modifier)
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
      val textStyle = MaterialTheme.typography.labelSmall
      Text(
        text = name,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = textStyle,
        color = Color.White,
      )
      val fontSizeWithSpacing = (textStyle.fontSize.value + 5).sp
      AutoResizedText(
        text = total,
        minFontSize = 8.sp,
        maxLines = 1,
        style = textStyle,
        color = Color.White,
        modifier = Modifier.height(fontSizeWithSpacing.toDp())
      )
    }
  }
}

@Composable
fun AddCategoryItem(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  // Todo do custom ripple effect animation
  Box(modifier = Modifier.height(IntrinsicSize.Max)) {
    // Dummy category item to match CategoryItem size
    CategoryItem(
      name = "Childcare",
      color = Color.White,
      icon = AccountIcons.ChildCare,
      total = "$100",
      onClick = { },
      isEditMode = { false },
      onDeleteClick = {},
      modifier = Modifier.alpha(0f),
    )
    Icon(
      imageVector = ExpeIcons.Add,
      contentDescription = "icon",
      modifier = modifier
        .fillMaxSize()
        .padding(ItemPadding)
        .clip(RoundedCornerNormalRadiusShape)
        .clickable(onClick = onClick)
        .background(color = MaterialTheme.colorScheme.primaryContainer)
        .padding(Dimens.margin_large_x)
        .padding(Dimens.margin_small_x),
    )
  }
}