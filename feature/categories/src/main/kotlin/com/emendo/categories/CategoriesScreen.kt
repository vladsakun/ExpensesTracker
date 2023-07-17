package com.emendo.categories

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons

data class Category(
  val id: Int,
  val name: String,
  val icon: ImageVector
)

sealed class CategoryItemType {
  object AddCategory : CategoryItemType()
  object Summary : CategoryItemType()
  data class Category(
    val id: Int,
    val name: String,
    val icon: ImageVector
  ) : CategoryItemType()
}

@OptIn(ExperimentalFoundationApi::class)
@Destination(start = true)
@Composable
fun CategoriesScreen(
  navigator: DestinationsNavigator
) {

  val categories = remember {
    mutableStateListOf(
      CategoryItemType.Category(1, "Category 1", ExpIcons.Accounts),
      CategoryItemType.Category(2, "Category 2", ExpIcons.Accounts),
      CategoryItemType.Category(3, "Category 3", ExpIcons.Accounts),
      CategoryItemType.Category(4, "Category 4", ExpIcons.Accounts),
      CategoryItemType.Category(5, "Category 5", ExpIcons.Accounts),
      CategoryItemType.Summary,
      CategoryItemType.Category(7, "Category 7", ExpIcons.Accounts),
      CategoryItemType.Category(7, "Category 7", ExpIcons.Accounts),
      CategoryItemType.Category(8, "Category 77", ExpIcons.Accounts),
      CategoryItemType.Category(8, "Category 8", ExpIcons.Accounts),
      CategoryItemType.Category(9, "Category 9", ExpIcons.Accounts),
      CategoryItemType.Category(10, "Category 10", ExpIcons.Accounts),
      CategoryItemType.Category(11, "Category 11", ExpIcons.Accounts),
      CategoryItemType.AddCategory,
    )
  }

  val cellSizeDp = (LocalConfiguration.current.screenWidthDp / 4).dp
  val screenWidthHalf = LocalConfiguration.current.screenWidthDp / 2

  LazyColumn {
    item {
      Row {
        categories.take(4).forEach {
          CategoryScreenItem(it, cellSizeDp, categories)
        }
      }
    }

    item {
      Row(
        modifier = Modifier
          .width(cellSizeDp * 4)
          .height(cellSizeDp * 2)
      ) {
        Column(
          modifier = Modifier
            .width(cellSizeDp)
        ) {
          Box(
            modifier = Modifier
              .size(cellSizeDp)
              .padding(20.dp)
              .background(Color.Red)
          )
          Box(
            modifier = Modifier
              .size(cellSizeDp)
              .padding(20.dp)
              .background(Color.Red)
          )
        }
        Box(
          modifier = Modifier
            .width(cellSizeDp * 2)
            .height(cellSizeDp * 2)
            .padding(20.dp)
            .background(Color.Blue)
        )
        Column(
          modifier = Modifier
            .width(cellSizeDp)
        ) {
          Box(
            modifier = Modifier
              .size(cellSizeDp)
              .padding(20.dp)
              .background(Color.Red)
          )
          Box(
            modifier = Modifier
              .size(cellSizeDp)
              .padding(20.dp)
              .background(Color.Red)
          )
        }
      }
    }

    categories.chunked(4).forEach {
      item {
        Row(modifier = Modifier.fillMaxWidth()) {
          it.forEach {
            CategoryScreenItem(it, cellSizeDp, categories)
          }
        }
      }
    }
  }
}

@Composable
private fun CategoryScreenItem(
  it: CategoryItemType,
  cellSizeDp: Dp,
  categories: SnapshotStateList<CategoryItemType>
) {
  when (it) {
    is CategoryItemType.AddCategory -> AddCategoryItem(
      cellSizeDp = cellSizeDp,
      onClick = { categories.add(CategoryItemType.Category(1, "test", ExpIcons.Accounts)) }
    )

    is CategoryItemType.Category -> {
      CategoryItem(category = it, cellSizeDp = cellSizeDp)
    }

    is CategoryItemType.Summary -> {
      BudgetItem(cellSizeDp = cellSizeDp)
    }
  }
}

@Composable
fun BudgetItem(cellSizeDp: Dp) {
  Box(
    modifier = Modifier
      .size(cellSizeDp * 2)
      .padding(20.dp)
      .background(Color.Red)
  )
}

@Composable
fun CategoryItem(
  category: CategoryItemType.Category,
  cellSizeDp: Dp,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .size(cellSizeDp)
      .padding(20.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      imageVector = category.icon,
      contentDescription = "category icon",
      modifier = Modifier.clip(CircleShape)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(text = category.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
  }
}

@Composable
fun AddCategoryItem(
  cellSizeDp: Dp,
  onClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .size(cellSizeDp)
      .padding(20.dp)
      .background(Color.Yellow)
      .clickable { onClick() },
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      imageVector = ExpIcons.Add,
      contentDescription = "category icon",
      modifier = Modifier.clip(CircleShape)
    )
    Spacer(modifier = Modifier.height(4.dp))
  }
}