package com.emendo.expensestracker.categories.detail

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.categories.common.*
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ScreenWithModalBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.handleValueResult
import com.emendo.expensestracker.model.ui.UiState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient

@Destination
@Composable
fun CategoryDetailScreen(
  navigator: DestinationsNavigator,
  // retrieved in ViewModel via SavedStateHandle
  @Suppress("UNUSED_PARAMETER") categoryId: Long,
  colorResultRecipient: OpenResultRecipient<Int>,
  iconResultRecipient: OpenResultRecipient<Int>,
  viewModel: CategoryDetailViewModel = hiltViewModel(),
) {
  colorResultRecipient.handleValueResult(viewModel::updateColor)
  iconResultRecipient.handleValueResult(viewModel::updateIcon)

  ScreenWithModalBottomSheet(
    stateManager = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    bottomSheetContent = { BottomSheetContent(it) }
  ) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    CategoryDetailContent(
      stateProvider = state::value,
      onNavigationClick = navigator::navigateUp,
      commandProcessor = viewModel::processCommand,
    )
  }
}

@Composable
private fun CategoryDetailContent(
  stateProvider: () -> UiState<CategoryDetailScreenDataImpl>,
  onNavigationClick: () -> Unit,
  commandProcessor: (CategoryCommand) -> Unit,
) {
  when (val state = stateProvider()) {
    is UiState.Data -> {
      CategoryContent(
        title = stringResource(id = R.string.category),
        stateProvider = { state.data.categoryScreenData },
        onNavigationClick = onNavigationClick,
        onTitleChanged = { commandProcessor(UpdateTitleCategoryCommand(it)) },
        onIconSelectClick = { commandProcessor(OpenSelectIconScreenCategoryCommand()) },
        onColorSelectClick = { commandProcessor(OpenSelectColorScreenCategoryCommand()) },
        onConfirmActionClick = { commandProcessor(UpdateCategoryCategoryDetailCommand()) },
        confirmButtonText = stringResource(id = R.string.save)
      ) {
        ExpeButton(
          textResId = R.string.delete,
          onClick = { commandProcessor(ShowDeleteCategoryBottomSheetCategoryDetailCommand()) },
          colors = ButtonDefaults.textButtonColors(),
        )
      }
    }

    else -> {
      // Todo handle Empty/Error states
      Unit
    }
  }
}

@Composable
private fun ColumnScope.BottomSheetContent(type: BottomSheetData) {
  when (type) {
    is GeneralBottomSheetData -> GeneralBottomSheet(type)
  }
}