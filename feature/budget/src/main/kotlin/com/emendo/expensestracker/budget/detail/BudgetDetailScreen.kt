package com.emendo.expensestracker.budget.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.MenuAction
import com.emendo.expensestracker.core.designsystem.component.VerticalSpacer
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color
import com.emendo.expensestracker.model.ui.NetworkViewState
import com.emendo.expensestracker.model.ui.successData
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.persistentListOf

@Destination
@Composable
fun BudgetDetailRoute(
  budgetId: Long,
  navigator: DestinationsNavigator,
  viewModel: BudgetDetailViewModel = hiltViewModel(),
) {
  val periods = viewModel.periodsFlow.collectAsStateWithLifecycle()
  val selectedPeriodIndex = viewModel.selectedPeriodIndexFlow.collectAsStateWithLifecycle()
  val budgetValueState = viewModel.budgetValueFlow.collectAsStateWithLifecycle()
  val isDeleted = viewModel.isDeleted.collectAsStateWithLifecycle()
  var showDeleteDialog by remember { mutableStateOf(false) }

  // If deleted, navigate back
  LaunchedEffect(isDeleted.value) {
    if (isDeleted.value) {
      navigator.popBackStack()
    }
  }

  val actions = persistentListOf(
    MenuAction(
      icon = ExpeIcons.Delete,
      onClick = { showDeleteDialog = true },
      contentDescription = stringResource(id = R.string.delete),
    )
  )

  ExpeScaffoldWithTopBar(
    title = budgetValueState.value.successData?.budget?.name?.stringValue() ?: "",
    onNavigationClick = navigator::popBackStack,
    actions = actions,
  ) { paddingValues ->
    if (showDeleteDialog) {
      AlertDialog(
        onDismissRequest = { showDeleteDialog = false },
        title = { Text(stringResource(id = R.string.delete)) },
        text = { Text(stringResource(id = R.string.delete_budget_confirmation)) },
        confirmButton = {
          TextButton(onClick = {
            showDeleteDialog = false
            viewModel.deleteBudget()
          }) {
            Text(stringResource(id = R.string.confirm))
          }
        },
        dismissButton = {
          TextButton(onClick = { showDeleteDialog = false }) {
            Text(stringResource(id = R.string.cancel))
          }
        }
      )
    }
    when (val state = budgetValueState.value) {
      is NetworkViewState.Success -> {
        BudgetDetailScreen(
          periodsProvider = periods::value,
          selectedPeriodIndexProvider = selectedPeriodIndex::value,
          budgetValue = state.data,
          paddingValues = paddingValues,
          onPeriodSelected = viewModel::setSelectedPeriodIndex,
        )
      }

      is NetworkViewState.Loading -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues), contentAlignment = Alignment.Center
        ) {
          Text("Loading...", color = Color.Gray)
        }
      }

      is NetworkViewState.Error -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues), contentAlignment = Alignment.Center
        ) {
          Text("Error: ${state.message}", color = Color.Red)
        }
      }

      else -> {}
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetDetailScreen(
  periodsProvider: () -> List<BudgetPeriod.Month>,
  selectedPeriodIndexProvider: () -> Int,
  budgetValue: BudgetScreenData,
  paddingValues: PaddingValues,
  onPeriodSelected: (Int) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(paddingValues)
  ) {
    Periods(selectedPeriodIndexProvider, periodsProvider, onPeriodSelected)
    VerticalSpacer(Dimens.margin_large_x)
    BudgetCard(budgetValue)
  }
}

@Composable
private fun BudgetCard(data: BudgetScreenData) {
  Card(
    modifier = Modifier
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  ) {
    Row(
      modifier = Modifier.padding(Dimens.margin_large_x),
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(data.budget.color.color.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = data.budget.icon.imageVector,
          contentDescription = null,
          tint = data.budget.color.color,
          modifier = Modifier.size(28.dp)
        )
      }
      Column(
        modifier = Modifier.padding(horizontal = Dimens.margin_small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
      ) {
        Row {
          Text(
            text = data.budget.name.stringValue(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
          )
          Text(
            text = "${data.spent} / ${data.limit}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
          )
        }
        LinearProgressIndicator(
          progress = { data.progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(MaterialTheme.shapes.extraLarge),
          color = MaterialTheme.colorScheme.primary,
        )
      }
    }
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Periods(
  selectedPeriodIndexProvider: () -> Int,
  periodsProvider: () -> List<BudgetPeriod.Month>,
  onPeriodSelected: (Int) -> Unit,
) {
  PrimaryScrollableTabRow(
    selectedTabIndex = selectedPeriodIndexProvider(),
    edgePadding = 0.dp,
    modifier = Modifier.fillMaxWidth(),
  ) {
    periodsProvider().forEachIndexed { index, period ->
      Text(
        text = period.label.stringValue(),
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .clickable(onClick = { onPeriodSelected(index) })
          .padding(Dimens.margin_large_x),
      )
    }
  }
}
