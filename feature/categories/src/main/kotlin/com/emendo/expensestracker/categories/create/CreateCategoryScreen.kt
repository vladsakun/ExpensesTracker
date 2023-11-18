package com.emendo.expensestracker.categories.create

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
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.ExpeTextField
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.SelectRowWithColor
import com.emendo.expensestracker.core.ui.SelectRowWithIcon
import com.emendo.expensestracker.core.ui.bottomsheet.ColorsBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.IconsBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseScreenWithModalBottomSheetWithViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun CreateCategoryRoute(
  navigator: DestinationsNavigator,
  @Suppress("UNUSED_PARAMETER") categoryType: CategoryType, // retrieved in VM via SavedStateHandle
  viewModel: CreateCategoryViewModel = hiltViewModel(),
) {
  val uiState = viewModel.state.collectAsStateWithLifecycle()

  BaseScreenWithModalBottomSheetWithViewModel(
    viewModel = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    bottomSheetContent = { type, hideBottomSheet -> BottomSheetContent(type, hideBottomSheet) },
  ) {
    CreateCategoryContent(
      stateProvider = uiState::value,
      onNavigationClick = navigator::navigateUp,
      onTitleChanged = viewModel::changeTitle,
      onIconSelectClick = viewModel::showIconBottomSheet,
      onColorSelectClick = viewModel::showColorBottomSheet,
      onCreateCategoryClick = viewModel::createCategory,
    )
  }
}

@Composable
private fun CreateCategoryContent(
  stateProvider: () -> CreateCategoryScreenData,
  onNavigationClick: () -> Unit,
  onTitleChanged: (String) -> Unit,
  onIconSelectClick: () -> Unit,
  onColorSelectClick: () -> Unit,
  onCreateCategoryClick: () -> Unit,
) {
  val scrollState = rememberScrollState()
  val isCreateButtonEnabled = remember { derivedStateOf { stateProvider().isCreateButtonEnabled } }

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
        text = stateProvider().title,
        onValueChange = onTitleChanged,
      )
      SelectRowWithIcon(
        labelResId = R.string.icon,
        imageVectorProvider = { stateProvider().icon.imageVector },
        onClick = onIconSelectClick,
      )
      SelectRowWithColor(
        labelResId = R.string.color,
        colorProvider = { stateProvider().color.darkColor },
        onClick = onColorSelectClick,
      )
      ExpeButton(
        textResId = R.string.create,
        onClick = onCreateCategoryClick,
        enabled = isCreateButtonEnabled.value,
      )
    }
  }
}

@Composable
private fun BottomSheetContent(
  type: BottomSheetType?,
  hideBottomSheet: () -> Unit,
) {
  when (type) {
    is BottomSheetType.Color -> {
      ColorsBottomSheet(
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
