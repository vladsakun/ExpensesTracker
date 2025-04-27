package com.emendo.expensestracker.report.subcategories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.VerticalSpacer
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.designsystem.utils.paddingWithoutBottom
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.model.ui.*
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color
import com.emendo.expensestracker.report.CategoryItem
import com.emendo.expensestracker.report.Period
import com.emendo.expensestracker.report.subcategories.ReportSubcategoriesScreenData.SubcategoryUiModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import de.palm.composestateevents.NavigationEventEffect
import kotlinx.collections.immutable.ImmutableList

data class ReportSubcategoriesScreenNavArgs(
  val categoryId: Long,
  val period: Period,
)

@Destination(navArgsDelegate = ReportSubcategoriesScreenNavArgs::class)
@Composable
internal fun ReportSubcategoriesRoute(
  navigator: DestinationsNavigator,
  viewModel: ReportSubcategoriesViewModel = hiltViewModel(),
) {
  val state = viewModel.state.collectAsStateWithLifecycle()
  val navigation = viewModel.navigationEvent.collectAsStateWithLifecycle()

  NavigationEventEffect(
    event = navigation.value,
    onConsumed = viewModel::onConsumedNavigationEvent,
    action = navigator::navigate,
  )

  ReportSubcategoriesContent(
    stateProvider = state::value,
    backClick = navigator::navigateUp,
    commandProcessor = viewModel::proceedCommand,
  )
}

@Composable
private fun ReportSubcategoriesContent(
  stateProvider: () -> NetworkViewState<ReportSubcategoriesScreenData>,
  backClick: () -> Unit,
  commandProcessor: (ReportSubcategoriesCommand) -> Unit,
) {
  ExpeScaffoldWithTopBar(
    title = stateProvider().successData?.categoryName.valueOrBlank(),
    onNavigationClick = backClick,
  ) { paddingValues ->
    // TODO statefulLayout
    Box(
      modifier = Modifier
        .fillMaxSize()
        .paddingWithoutBottom(paddingValues),
    ) {
      when (val stateValue = stateProvider()) {
        is NetworkViewState.Error -> Text(text = stateValue.message.stringValue())
        is NetworkViewState.Loading -> ExpLoadingWheel(modifier = Modifier.align(Alignment.Center))
        is NetworkViewState.Success -> ReportSubcategoriesContent(
          state = stateValue.data,
          bottomPadding = paddingValues.calculateBottomPadding(),
          commandProcessor = commandProcessor,
        )
      }
    }
  }
}

@Composable
private fun ReportSubcategoriesContent(
  state: ReportSubcategoriesScreenData,
  bottomPadding: Dp,
  commandProcessor: (ReportSubcategoriesCommand) -> Unit,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(
      top = Dimens.margin_large_x,
      bottom = Dimens.margin_large_x + bottomPadding,
    )
  ) {
    reportSum(
      label = state.reportSumLabel,
      sum = state.reportSum,
      transactionType = state.transactionType,
      onClick = { commandProcessor(OpenAllTransactionsCommand()) },
    )
    subcategoryItems(
      subcategories = state.subcategories,
      color = state.color,
      transactionType = state.transactionType,
      onItemClick = { commandProcessor(OpenSubcategoryTransactionsCommand(it.id)) },
    )
  }
}

private fun LazyListScope.reportSum(
  label: TextValue,
  sum: Amount,
  transactionType: TransactionType,
  onClick: () -> Unit,
) {
  uniqueItem("reportSum") {
    CategoryItem(
      icon = ExpeIcons.Functions,
      tint = MaterialTheme.customColorsPalette.neutralColor,
      amount = sum,
      name = label.stringValue(),
      transactionType = transactionType,
      onClick = onClick,
    )
    ExpeDivider()
    VerticalSpacer(Dimens.margin_large_x)
  }
}

private fun LazyListScope.subcategoryItems(
  subcategories: ImmutableList<SubcategoryUiModel>,
  color: ColorModel,
  transactionType: TransactionType,
  onItemClick: (SubcategoryUiModel) -> Unit,
) {
  items(
    items = subcategories,
    contentType = { "categoryItem" },
    key = SubcategoryUiModel::id,
  ) { categoryExpense ->
    CategoryItem(
      icon = categoryExpense.icon.imageVector,
      tint = color.color,
      amount = categoryExpense.sum,
      name = categoryExpense.name,
      transactionType = transactionType,
      onClick = { onItemClick(categoryExpense) },
    )
    ExpeDivider()
  }
}