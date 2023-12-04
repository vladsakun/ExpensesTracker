package com.emendo.expensestracker.categories.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectColorScreenDestination
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectIconScreenDestination
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.ExpeTextField
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.SelectRowWithColor
import com.emendo.expensestracker.core.ui.SelectRowWithIcon
import com.emendo.expensestracker.core.ui.handleValueResult
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

  NavigationEventEffect(
    event = state.value.navigateUpEvent,
    onConsumed = viewModel::consumeNavigateUpEvent,
    action = navigator::navigateUp,
  )

  CreateCategoryContent(
    stateProvider = state::value,
    onNavigationClick = navigator::navigateUp,
    onTitleChanged = remember { viewModel::changeTitle },
    onIconSelectClick = remember { { navigator.navigate(SelectIconScreenDestination(viewModel.selectedIconId)) } },
    onColorSelectClick = remember { { navigator.navigate(SelectColorScreenDestination(viewModel.selectedColorId)) } },
    onCreateCategoryClick = remember { viewModel::createCategory },
  )
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
  ExpeScaffoldWithTopBar(
    titleResId = R.string.create_category,
    onNavigationClick = onNavigationClick,
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .verticalScroll(rememberScrollState())
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
        colorProvider = { stateProvider().color },
        onClick = onColorSelectClick,
      )
      ExpeButton(
        textResId = R.string.create,
        onClick = onCreateCategoryClick,
        enabled = stateProvider().isCreateButtonEnabled,
      )
    }
  }
}
