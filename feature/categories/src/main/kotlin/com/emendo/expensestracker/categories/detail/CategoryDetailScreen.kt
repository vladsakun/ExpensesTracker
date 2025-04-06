package com.emendo.expensestracker.categories.detail

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.categories.common.CategoryContent
import com.emendo.expensestracker.categories.common.command.CategoryCommand
import com.emendo.expensestracker.categories.common.command.UpdateTitleCategoryCommand
import com.emendo.expensestracker.categories.destinations.CreateSubcategoryRouteDestination
import com.emendo.expensestracker.categories.subcategory.CreateSubcategoryResult
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ScreenWithModalBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.handleValueResult
import com.emendo.expensestracker.model.ui.UiState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient

// @NonSkippableComposable
@Destination
@Composable
fun CategoryDetailRoute(
    navigator: DestinationsNavigator,
    // retrieved in ViewModel via SavedStateHandle
    @Suppress("UNUSED_PARAMETER") categoryId: Long,
    colorResultRecipient: OpenResultRecipient<Int>,
    iconResultRecipient: OpenResultRecipient<Int>,
    subcategoryResultRecipient: OpenResultRecipient<CreateSubcategoryResult>,
    viewModel: CategoryDetailViewModel = hiltViewModel(),
) {
    colorResultRecipient.handleValueResult(viewModel::updateColor)
    iconResultRecipient.handleValueResult(viewModel::updateIcon)
    subcategoryResultRecipient.handleValueResult(viewModel::addSubcategory)

    ScreenWithModalBottomSheet(
        stateManager = viewModel,
        onNavigateUpClick = navigator::navigateUp,
        bottomSheetContent = { BottomSheetContent(it) },
    ) {
        val state = viewModel.state.collectAsStateWithLifecycle()

        CategoryDetailContent(
            stateProvider = state::value,
            onNavigationClick = navigator::navigateUp,
            commandProcessor = viewModel::processCommand,
            onIconSelectClick = remember { { navigator.navigate(viewModel.getSelectIconScreenRoute()) } },
            onColorSelectClick = remember { { navigator.navigate(viewModel.getSelectColorScreenRoute()) } },
            onAddSubcategoryClick = { navigator.navigate(CreateSubcategoryRouteDestination(viewModel.selectedColorId)) }
        )
    }
}

@Composable
private fun CategoryDetailContent(
    stateProvider: () -> UiState<CategoryDetailScreenDataImpl>,
    onNavigationClick: () -> Unit,
    commandProcessor: (CategoryCommand) -> Unit,
    onIconSelectClick: () -> Unit,
    onColorSelectClick: () -> Unit,
    onAddSubcategoryClick: () -> Unit,
) {
    when (val state = stateProvider()) {
        is UiState.Data -> {
            CategoryContent(
                title = stringResource(id = R.string.category),
                stateProvider = { state.data.categoryScreenData },
                onNavigationClick = onNavigationClick,
                onTitleChanged = { commandProcessor(UpdateTitleCategoryCommand(it)) },
                onIconSelectClick = onIconSelectClick,
                onColorSelectClick = onColorSelectClick,
                onConfirmActionClick = { commandProcessor(UpdateCategoryCategoryDetailCommand()) },
                onAddSubcategoryClick = onAddSubcategoryClick,
                confirmButtonText = stringResource(id = R.string.save),
            ) {
                ExpeButton(
                    textResId = R.string.delete,
                    onClick = { commandProcessor(ShowDeleteCategoryBottomSheetCategoryDetailCommand()) },
                    colors = ButtonDefaults.textButtonColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.margin_large_x),
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
