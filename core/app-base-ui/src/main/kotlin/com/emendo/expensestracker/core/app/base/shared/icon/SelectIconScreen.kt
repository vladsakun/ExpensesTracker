package com.emendo.expensestracker.core.app.base.shared.icon

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectIconScreenDestination
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.conditional
import com.emendo.expensestracker.core.ui.Constants
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.scope.AnimatedDestinationScope
import com.ramcosta.composedestinations.scope.resultRecipient
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Destination
@Composable
fun SelectIconScreen(
  navigator: DestinationsNavigator,
  resultNavigator: ResultBackNavigator<Int>,
  selectedIconId: Int,
) {
  val icons: ImmutableList<IconModel> = IconModel.entries.toImmutableList()
  ExpeScaffoldWithTopBar(
    titleResId = R.string.select_icon,
    onNavigationClick = navigator::navigateUp,
  ) { paddingValues ->
    LazyVerticalGrid(
      columns = GridCells.FixedSize(Constants.ITEM_FIXED_SIZE_DP.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      items(
        items = icons,
        key = IconModel::id,
        contentType = { _ -> "icons" },
      ) { icon ->
        IconItem(
          icon = icon,
          isSelected = icon.id == selectedIconId,
          onIconSelect = { resultNavigator.navigateBack(icon.id) },
        )
      }
    }
  }
}

@Composable
private fun IconItem(
  icon: IconModel,
  isSelected: Boolean,
  onIconSelect: (icon: IconModel) -> Unit,
) {
  val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = Constants.SELECTED_ITEM_ALPHA_BORDER)

  Surface {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .aspectRatio(1f)
        .padding(Dimens.margin_small_xx)
        .clip(CircleShape)
        .clickable { onIconSelect(icon) }
        .conditional(isSelected) {
          border(
            width = Constants.SELECTED_ICON_BORDER_WIDTH.dp,
            color = borderColor,
            shape = CircleShape,
          )
        }
    ) {
      Icon(
        imageVector = icon.imageVector,
        contentDescription = icon.name,
      )
    }
  }
}

@Composable
fun AnimatedDestinationScope<*>.selectIconResultRecipient() =
  resultRecipient<SelectIconScreenDestination, Int>()