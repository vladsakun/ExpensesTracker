package com.emendo.expensestracker.core.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.zIndex
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
      Icon(
        imageVector = ExpeIcons.Remove,
        contentDescription = "remove",
        modifier = Modifier
          .size(Dimens.icon_size)
          .zIndex(2f)
          .clickable { }
          .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
          .align(Alignment.TopStart)
      )
    }
    Column(
      modifier = modifier
        .fillMaxSize()
        .graphicsLayer {
          if (isEditMode()) {
            rotationZ = transition.value
            translationX = transition.value
          }
        }
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

//@Composable
//fun RoundedRectangleItem(
//  cellSizeDp: Dp,
//  icon: ImageVector,
//  backgroundColor: Color,
//  modifier: Modifier = Modifier,
//  onClick: () -> Unit = {},
//  text: String = "",
//  isEditMode: Boolean,
//) {
//
//  Column(
//    modifier = modifier
//      .width(cellSizeDp)
//      .wrapContentHeight()
//      .padding(PADDING_CATEGORY_ITEM_PADDING)
//      .graphicsLayer {
//        if (isEditMode) {
//          rotationZ = rotation
//        }
//      },
//    verticalArrangement = Arrangement.Center,
//    horizontalAlignment = Alignment.CenterHorizontally
//  ) {
//    Box {
//      if (isEditMode) {
//        Icon(
//          imageVector = ExpIcons.Remove,
//          contentDescription = "remove",
//          modifier = Modifier
//            .size(16.dp)
//            .zIndex(2f)
//            .background(Color.Gray, CircleShape)
//            .align(Alignment.TopStart)
//        )
//      }
//      Box(
//        modifier = Modifier
//          .fillMaxWidth()
//          .padding(4.dp)
//          .aspectRatio(1f)
//          .clip(RoundedCornerShape(16.dp))
//          .background(backgroundColor, RectangleShape)
//          .clickable(onClick = onClick)
//      ) {
//        Icon(
//          imageVector = icon,
//          contentDescription = "category icon",
//          modifier = Modifier
//            .fillMaxSize(0.6f)
//            .align(Alignment.Center)
//        )
//      }
//    }
//    Spacer(modifier = Modifier.height(4.dp))
//    Text(
//      text = text,
//      maxLines = 1,
//      overflow = TextOverflow.Ellipsis,
//      style = MaterialTheme.typography.labelSmall
//    )
//  }
//}