package com.emendo.categories.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.ExpeTextField
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.SelectRowWithColor
import com.emendo.expensestracker.core.ui.SelectRowWithIcon
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetType
import com.emendo.expensestracker.core.ui.bottomsheet.ColorsBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.IconsBottomSheet
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow

@Destination
@Composable
fun CreateCategoryRoute(
  navigator: DestinationsNavigator,
  viewModel: CreateCategoryViewModel = hiltViewModel(),
) {
  BaseScreenWithModalBottomSheetWithViewModel(
    viewModelWithBottomSheet = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    screenContent = {
      CreateCategoryContent(
        stateFlow = viewModel.state,
        onNavigationClick = navigator::navigateUp,
        onTitleChanged = viewModel::onTitleChanged,
        onIconSelectClick = viewModel::onIconSelectClick,
        onColorSelectClick = viewModel::onColorSelectClick,
        onCreateCategoryClick = viewModel::onCreateCategoryClick,
      )
    },
    bottomSheetContent = { type, closeBottomSheet ->
      BottomSheetContent(type, closeBottomSheet)
    },
  )
}

@Composable
private fun BottomSheetContent(
  type: BottomSheetType?,
  hideBottomSheet: () -> Unit,
) {
  when (type) {
    is BottomSheetType.Color -> {
      ColorsBottomSheet(
        colors = ColorModel.entries.toImmutableList(),
        selectedColor = type.selectedColor,
        onColorSelect = {
          type.onSelectColor(it)
          hideBottomSheet()
        },
        onCloseClick = hideBottomSheet,
      )
    }

    is BottomSheetType.Icon -> {
      IconsBottomSheet(
        icons = IconModel.entries.toImmutableList(),
        onIconSelect = {
          type.onSelectIcon(it)
          hideBottomSheet()
        },
        onCloseClick = hideBottomSheet,
        selectedIcon = type.selectedIcon,
      )
    }
  }
}

@Composable
private fun CreateCategoryContent(
  stateFlow: StateFlow<CreateCategoryScreenData>,
  onNavigationClick: () -> Unit,
  onTitleChanged: (String) -> Unit,
  onIconSelectClick: () -> Unit,
  onColorSelectClick: () -> Unit,
  onCreateCategoryClick: () -> Unit,
) {
  val state = stateFlow.collectAsStateWithLifecycle()
  val scrollState = rememberScrollState()
  val isCreateButtonEnabled = remember { derivedStateOf { state.value.isCreateButtonEnabled } }

  ExpeScaffoldWithTopBar(
    titleResId = R.string.create_category,
    onNavigationClick = onNavigationClick,
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .verticalScroll(scrollState)
        .padding(paddingValues)
        .padding(Dimens.margin_large_x),
      verticalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
    ) {
      ExpeTextField(
        label = "Title",
        text = state.value.title,
        onValueChange = onTitleChanged,
      )
      SelectRowWithIcon(
        labelResId = R.string.icon,
        imageVector = state.value.icon.imageVector,
        onClick = onIconSelectClick,
      )
      SelectRowWithColor(
        labelResId = R.string.color,
        color = state.value.color.color,
        onClick = onColorSelectClick,
      )
      Spacer(Modifier.padding(vertical = Dimens.margin_large_x))
      ExpeButton(
        textResId = R.string.create,
        onClick = onCreateCategoryClick,
        enabled = isCreateButtonEnabled.value,
      )
    }
  }
}
