package com.emendo.expensestracker.budget.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.budget.destinations.CreateBudgetRouteDestination
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun BudgetListRoute(
  navigator: DestinationsNavigator,
  viewModel: BudgetListViewModel = hiltViewModel(),
) {
  val budgetsUi = viewModel.budgetsUi.collectAsStateWithLifecycle()
  BudgetListScreen(
    budgets = budgetsUi.value,
    onAddBudgetClick = { navigator.navigate(CreateBudgetRouteDestination) },
  )
}

@Composable
private fun BudgetListScreen(
  budgets: List<BudgetModelUi>,
  onAddBudgetClick: () -> Unit,
) {
  ExpeScaffoldWithTopBar(
    titleResId = R.string.budget,
    floatingActionButtonPosition = FabPosition.End,
    floatingActionButton = {
      ExtendedFloatingActionButton(
        text = { Text(stringResource(id = R.string.create_budget)) },
        icon = {
          Icon(
            imageVector = ExpeIcons.Add,
            contentDescription = stringResource(id = R.string.create_budget),
          )
        },
        onClick = onAddBudgetClick,
      )
    },
  ) { padding ->
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = padding,
    ) {
      items(budgets) { budgetUi ->
        BudgetListItem(budgetUi)
      }
    }
  }
}

@Composable
private fun BudgetListItem(budgetUi: BudgetModelUi) {
  val budget = budgetUi.budget
  Card(
    modifier = Modifier
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(budget.color.color.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = budget.icon.imageVector,
          contentDescription = null,
          tint = budget.color.color,
          modifier = Modifier.size(28.dp)
        )
      }
      Spacer(modifier = Modifier.width(16.dp))
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = budget.name.value,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
          progress = budgetUi.percent,
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "${budgetUi.spent} / ${budgetUi.limit}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }
  }
}
