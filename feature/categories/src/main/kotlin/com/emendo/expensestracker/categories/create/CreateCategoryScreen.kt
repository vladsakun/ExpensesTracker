package com.emendo.expensestracker.categories.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.categories.common.CategoryContent
import com.emendo.expensestracker.core.ui.handleValueResult
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.model.ui.UiState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient
import de.palm.composestateevents.NavigationEventEffect

@Destination
@Composable
fun CreateCategoryRoute(
  navigator: DestinationsNavigator,
  // retrieved in ViewModel via SavedStateHandle
  @Suppress("UNUSED_PARAMETER") categoryType: CategoryType,
  colorResultRecipient: OpenResultRecipient<Int>,
  iconResultRecipient: OpenResultRecipient<Int>,
  viewModel: CreateCategoryViewModel = hiltViewModel(),
) {
  colorResultRecipient.handleValueResult(viewModel::updateColor)
  iconResultRecipient.handleValueResult(viewModel::updateIcon)

  val state = viewModel.state.collectAsStateWithLifecycle()

  CreateCategoryContent(
    stateProvider = state::value,
    onNavigationClick = navigator::navigateUp,
    consumeNavigationEvent = remember { viewModel::consumeNavigateUpEvent },
    onTitleChanged = remember { viewModel::changeTitle },
    onIconSelectClick = remember { viewModel::openSelectIconScreen },
    onColorSelectClick = remember { viewModel::openSelectColorScreen },
    onConfirmActionClick = remember { viewModel::createCategory },
  )
}

@Composable
private fun CreateCategoryContent(
  stateProvider: () -> UiState<CreateCategoryScreenData>,
  onNavigationClick: () -> Unit,
  consumeNavigationEvent: () -> Unit,
  onTitleChanged: (String) -> Unit,
  onIconSelectClick: () -> Unit,
  onColorSelectClick: () -> Unit,
  onConfirmActionClick: () -> Unit,
) {
  when (val state = stateProvider()) {
    is UiState.Data -> {
      NavigationEventEffect(
        event = checkNotNull(state.data.additionalData).navigateUpEvent,
        onConsumed = consumeNavigationEvent,
        action = onNavigationClick,
      )

      CategoryContent(
        title = stringResource(id = R.string.create_category),
        stateProvider = state::data,
        onNavigationClick = onNavigationClick,
        onTitleChanged = onTitleChanged,
        onIconSelectClick = onIconSelectClick,
        onColorSelectClick = onColorSelectClick,
        onConfirmActionClick = onConfirmActionClick,
        confirmButtonText = stringResource(id = R.string.create),
        shouldFocusTitleInputOnLaunch = true,
      )
    }

    else -> Unit
  }
}
