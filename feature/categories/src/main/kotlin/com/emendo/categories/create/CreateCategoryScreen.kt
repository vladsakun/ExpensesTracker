package com.emendo.categories.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.ExpeButton
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.ExpeTextField
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.SelectRowWithColor
import com.emendo.expensestracker.core.ui.SelectRowWithIcon
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetType
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Destination
@Composable
fun CreateCategoryRoute(
  navigator: DestinationsNavigator,
  viewModel: CreateCategoryViewModel = hiltViewModel(),
) {
  CreateCategoryScreen(
    state = viewModel.state.collectAsStateWithLifecycle(),
    bottomSheetType = viewModel.bottomSheetState.collectAsStateWithLifecycle(),
    hideBottomSheetEvent = viewModel.hideBottomSheetEvent,
    navigateUpEvent = viewModel.navigateUpEvent,
    onNavigationClick = navigator::navigateUp,
    onTitleChanged = viewModel::onTitleChanged,
    onIconSelectClick = viewModel::onIconSelectClick,
    onColorSelectClick = viewModel::onColorSelectClick,
    onCreateCategoryClick = viewModel::onCreateCategoryClick,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCategoryScreen(
  state: State<CreateCategoryScreenData>,
  bottomSheetType: State<BottomSheetType?>,
  hideBottomSheetEvent: Flow<Unit>,
  navigateUpEvent: Flow<Unit>,
  onNavigationClick: () -> Unit,
  onTitleChanged: (String) -> Unit,
  onIconSelectClick: () -> Unit,
  onColorSelectClick: () -> Unit,
  onCreateCategoryClick: () -> Unit,
) {
  val scrollState = rememberScrollState()
  val coroutineScope = rememberCoroutineScope()
  val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val closeBottomSheet: () -> Unit = remember(bottomSheetState) {
    {
      coroutineScope.launch {
        bottomSheetState.hide()
      }
    }
  }

  LaunchedEffect(Unit) { hideBottomSheetEvent.collect { closeBottomSheet() } }
  LaunchedEffect(Unit) { navigateUpEvent.collect { onNavigationClick() } }

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
        onClick = onCreateCategoryClick
      )
    }
  }
}