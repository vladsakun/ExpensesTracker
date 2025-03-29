package com.emendo.expensestracker.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusShape
import kotlin.random.Random

private val ItemPadding
  get() = Dimens.margin_small_x

@Composable
fun CategoryItem(
  name: String,
  color: Color,
  icon: ImageVector,
  onClick: () -> Unit,
  onDeleteClick: () -> Unit,
  editMode: () -> Boolean,
  modifier: Modifier = Modifier,
) {
  val infiniteTransition = rememberInfiniteTransition(label = "rotationTransition")
  val transition = infiniteTransition.animateFloat(
    initialValue = -4f,
    targetValue = 4f,
    animationSpec = infiniteRepeatable(
      animation = tween(200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse,
      initialStartOffset = StartOffset(Random.nextInt(0, 200))
    ),
    label = "rotation",
  )

  Box(
    modifier = Modifier
      .clickable(onClick = onClick)
      .padding(bottom = Dimens.margin_small_x),
  ) {
    AnimatedVisibility(
      visible = editMode(),
      modifier = Modifier
        .zIndex(2f)
        .graphicsLayer { translationY = -20f },
      enter = fadeIn(),
      exit = fadeOut(),
    ) {
      IconButton(onClick = onDeleteClick) {
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
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_xx),
      modifier = Modifier
        .align(Alignment.Center)
        .padding(ItemPadding)
        .graphicsLayer {
          if (editMode()) {
            rotationZ = transition.value
            translationX = transition.value
          }
        }
    ) {
      Icon(
        imageVector = icon,
        contentDescription = "$name category icon",
        tint = color,
        modifier = Modifier
          .clip(RoundedCornerNormalRadiusShape)
          .background(color.copy(alpha = 0.1f))
          .padding(Dimens.margin_normal)
          .then(modifier),
      )
      Text(
        text = name,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall,
      )
    }
  }
}

//@Composable
//fun AddCategoryItem(
//  onClick: () -> Unit,
//  modifier: Modifier = Modifier,
//) {
//  // Todo do custom ripple effect animation
//  Box(modifier = Modifier.height(IntrinsicSize.Max)) {
//    // Dummy category item to match CategoryItem size
//    CategoryItem(
//      name = "Childcare",
//      color = Color.White,
//      icon = AccountIcons.ChildCare,
//      onClick = { },
//      onDeleteClick = {},
//      editMode = { false },
//      modifier = Modifier.alpha(0f),
//    )
//    Icon(
//      imageVector = ExpeIcons.Add,
//      contentDescription = "icon",
//      modifier = modifier
//        .fillMaxSize()
//        .padding(ItemPadding)
//        .clip(RoundedCornerNormalRadiusShape)
//        .clickable(onClick = onClick)
//        .background(color = MaterialTheme.colorScheme.primaryContainer)
//        .padding(Dimens.margin_large_x + Dimens.margin_small_x),
//    )
//  }
//}