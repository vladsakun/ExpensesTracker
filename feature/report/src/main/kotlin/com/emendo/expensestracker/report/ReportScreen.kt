package com.emendo.expensestracker.report

import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerSmallRadiusShape
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.ui.AmountText
import com.emendo.expensestracker.core.ui.EmptyState
import com.emendo.expensestracker.core.ui.piechart.charts.DonutPieChart
import com.emendo.expensestracker.core.ui.piechart.models.PieChartConfig
import com.emendo.expensestracker.core.ui.piechart.models.PieChartData
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.data.api.extensions.labelResId
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color
import com.emendo.expensestracker.model.ui.NetworkViewState
import com.emendo.expensestracker.model.ui.TextValue
import com.emendo.expensestracker.model.ui.textValueOf
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import de.palm.composestateevents.NavigationEventEffect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant

@Destination(start = true)
@Composable
internal fun ReportRoute(
  navigator: DestinationsNavigator,
  viewModel: ReportViewModel = hiltViewModel(),
) {
  val state = viewModel.state.collectAsStateWithLifecycle()
  val selectedPeriod = viewModel.selectedPeriod.collectAsStateWithLifecycle()
  val showPickerDialog = viewModel.showPickerDialog.collectAsStateWithLifecycle()
  val selectedCategory = viewModel.selectedCategory.collectAsStateWithLifecycle()
  val navigation = viewModel.navigation.collectAsStateWithLifecycle()
  val periods = viewModel.periods.collectAsStateWithLifecycle()
  val selectedPeriodIndex = viewModel.selectedPeriodIndex.collectAsStateWithLifecycle()

  NavigationEventEffect(
    event = navigation.value.openCategoryTransactions,
    onConsumed = viewModel::onConsumedOpenCategoryTransactionsEvent,
    action = navigator::navigate,
  )

  ReportScreenContent(
    stateProvider = state::value,
    periodsProvider = periods::value,
    selectedPeriodIndexProvider = selectedPeriodIndex::value,
    selectedPeriodProvider = selectedPeriod::value,
    showPickerDialogProvider = showPickerDialog::value,
    selectedCategoryProvider = selectedCategory::value,
    commandProcessor = viewModel::proceedCommand,
  )
}

@Composable
private fun ReportScreenContent(
  stateProvider: () -> NetworkViewState<ReportScreenData>,
  periodsProvider: () -> ImmutableList<ReportPeriod>,
  selectedPeriodIndexProvider: () -> Int,
  selectedPeriodProvider: () -> ReportPeriod,
  showPickerDialogProvider: () -> Boolean,
  selectedCategoryProvider: () -> ReportScreenData.CategoryValue?,
  commandProcessor: (ReportScreenCommand) -> Unit,
) {
  ExpeScaffoldWithTopBar(titleResId = R.string.report_title) { paddingValues ->
    // TODO statefulLayout
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
    ) {
      when (val stateValue = stateProvider()) {
        is NetworkViewState.Idle -> Unit
        is NetworkViewState.Error -> Text(text = stateValue.message.stringValue())
        is NetworkViewState.Loading -> ExpLoadingWheel(modifier = Modifier.align(Alignment.Center))
        is NetworkViewState.Success -> {
          ReportScreenContent(
            state = stateValue.data,
            periodsProvider = periodsProvider,
            selectedPeriodIndexProvider = selectedPeriodIndexProvider,
            selectedPeriodProvider = selectedPeriodProvider,
            selectedCategoryProvider = selectedCategoryProvider,
            commandProcessor = commandProcessor,
          )

          DateRangePickerDialog(
            showDialogProvider = showPickerDialogProvider,
            commandProcessor = commandProcessor,
          )
        }
      }
    }
  }
}

@Composable
private fun ReportScreenContent(
  state: ReportScreenData,
  periodsProvider: () -> ImmutableList<ReportPeriod>,
  selectedPeriodIndexProvider: () -> Int,
  selectedPeriodProvider: () -> ReportPeriod,
  selectedCategoryProvider: () -> ReportScreenData.CategoryValue?,
  commandProcessor: (ReportScreenCommand) -> Unit,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(vertical = Dimens.margin_large_x),
  ) {
    typeSelection(
      commandProcessor = commandProcessor,
      transactionType = state.transactionType,
    )
    periods(
      reportPeriods = periodsProvider,
      selectedPeriodIndexProvider = selectedPeriodIndexProvider,
      commandProcessor = commandProcessor,
    )
    balance(
      balanceDate = state.balanceDate,
      balance = state.balance,
      transactionType = state.transactionType,
    )
    if (state.pieChartData == null) {
      emptyState()
    } else {
      pieChart(
        reportPieChartSlices = state.pieChartData,
        onSliceClick = { commandProcessor(SelectSlice(it.id)) }
      )
      reportSum(
        selectedCategoryProvider = selectedCategoryProvider,
        sum = state.reportSum,
        onClick = { commandProcessor(SelectCategory(it)) },
      )
      categoryItems(
        categoryValues = state.categoryValues,
        transactionType = state.transactionType,
        onItemClick = { commandProcessor(SelectCategory(SelectCategoryClickEvent.CategorySelected(it.categoryId))) }
      )
    }
  }
}

private fun LazyListScope.emptyState() {
  uniqueItem("emptyState") {
    EmptyState(
      modifier = Modifier
        .fillMaxWidth()
        .padding(Dimens.margin_large_x)
        .animateItem(),
    )
  }
}

private fun LazyListScope.periods(
  reportPeriods: () -> ImmutableList<ReportPeriod>,
  selectedPeriodIndexProvider: () -> Int,
  commandProcessor: (ReportScreenCommand) -> Unit,
) {
  uniqueItem("periods") {
    val selectedTabIndex = selectedPeriodIndexProvider()
    ScrollableTabRow(
      edgePadding = 0.dp,
      selectedTabIndex = selectedTabIndex,
      modifier = Modifier.wrapContentWidth(),
      indicator = { tabPositions ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .tabIndicatorOffset(tabPositions[selectedTabIndex])
            .clip(RoundedCornerSmallRadiusShape)
            .background(MaterialTheme.colorScheme.primary),
        )
      }
    ) {
      reportPeriods().forEach { period ->
        Text(
          text = period.label.stringValue(),
          style = MaterialTheme.typography.labelSmall,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .clickable(onClick = { commandProcessor(SetPeriodCommand(period)) })
            .padding(Dimens.margin_large_x),
        )
      }
    }
    VerticalSpacer(height = Dimens.margin_large_x)
  }
}

private fun LazyListScope.categoryItems(
  categoryValues: ImmutableList<ReportScreenData.CategoryValue>,
  transactionType: TransactionType,
  onItemClick: (ReportScreenData.CategoryValue) -> Unit,
) {
  items(
    items = categoryValues,
    contentType = { "categoryItem" },
    key = ReportScreenData.CategoryValue::categoryId,
  ) { categoryExpense ->
    CategoryItem(
      icon = categoryExpense.icon.imageVector,
      tint = categoryExpense.color.color,
      amount = categoryExpense.amount,
      name = categoryExpense.categoryName.stringValue(),
      transactionType = transactionType,
      onClick = { onItemClick(categoryExpense) },
    )
    ExpeDivider()
  }
}

private fun LazyListScope.reportSum(
  selectedCategoryProvider: () -> ReportScreenData.CategoryValue?,
  sum: ReportScreenData.ReportSum,
  onClick: (event: SelectCategoryClickEvent) -> Unit,
) {
  uniqueItem("reportSum") {
    val selectedCategory = selectedCategoryProvider()

    if (selectedCategory == null) {
      CategoryItem(
        icon = ExpeIcons.Functions,
        tint = MaterialTheme.customColorsPalette.neutralColor,
        amount = sum.value,
        name = sum.label.stringValue(),
        transactionType = sum.type,
        onClick = { onClick(SelectCategoryClickEvent.AllCategoriesSelected(sum.type)) },
      )
    } else {
      CategoryItem(
        icon = selectedCategory.icon.imageVector,
        tint = selectedCategory.color.color,
        amount = selectedCategory.amount,
        name = selectedCategory.categoryName.stringValue(),
        transactionType = sum.type,
        onClick = { onClick(SelectCategoryClickEvent.CategorySelected(selectedCategory.categoryId)) },
      )
    }
    ExpeDivider()
    VerticalSpacer(height = Dimens.margin_large_x)
  }
}

private fun LazyListScope.pieChart(
  reportPieChartSlices: ImmutableList<ReportPieChartSlice>,
  onSliceClick: (PieChartData.Slice) -> Unit,
) {
  uniqueItem("pieChart") {
    val pieChartData = PieChartData(
      reportPieChartSlices.map {
        PieChartData.Slice(it.categoryId, it.value, it.color.color)
      }.toImmutableList()
    )
    DonutPieChart(
      pieChartData = pieChartData,
      pieChartConfig = PieChartConfig(
        // Disable animation in Preview Mode
        isAnimationEnable = !LocalInspectionMode.current, // Todo replace with true
        chartPadding = 50,
        strokeWidth = 250f,
        sliceLabelTextSize = MaterialTheme.typography.labelMedium.fontSize,
        sliceLabelTextColor = Color.White,
        sliceLabelTypeface = Typeface.DEFAULT_BOLD,
      ),
      onSliceClick = onSliceClick,
      modifier = Modifier
        .padding(Dimens.margin_normal)
        .fillMaxWidth()
        .aspectRatio(1f)
        .animateItem(),
    )
  }
}

private fun LazyListScope.balance(
  balanceDate: TextValue,
  balance: Amount,
  transactionType: TransactionType,
) {
  uniqueItem("balance") {
    Row(
      horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = Dimens.margin_large_x)
        .animateItem(),
    ) {
      Text(
        text = balanceDate.stringValue(),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.weight(1f),
      )
      AmountText(
        amount = balance,
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.End,
        transactionType = transactionType,
      )
    }
  }
}

private fun LazyListScope.typeSelection(
  commandProcessor: (ReportScreenCommand) -> Unit,
  transactionType: TransactionType,
) {
  uniqueItem("typeSelection") {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = Dimens.margin_large_x)
        .padding(bottom = Dimens.margin_large_x),
      horizontalArrangement = Arrangement.End,
    ) {
      TypeSelectionItem(
        items = getTransactionTypeItems(commandProcessor = commandProcessor, transactionType = transactionType),
        text = stringResource(id = transactionType.labelResId),
      )
      // Todo add categories
    }
  }
}

@Composable
private fun TypeSelectionItem(
  items: ImmutableList<DropdownMenuItem>,
  text: String,
) {
  var expanded by remember { mutableStateOf(false) }
  Row(
    modifier = Modifier
      .clip(RoundedCornerSmallRadiusShape)
      .background(MaterialTheme.colorScheme.secondaryContainer)
      .clickable { expanded = !expanded },
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall,
      modifier = Modifier
        .padding(start = Dimens.margin_normal)
        .padding(vertical = Dimens.margin_normal),
    )
    Icon(
      imageVector = ExpeIcons.UnfoldMore,
      contentDescription = null,
      modifier = Modifier.padding(horizontal = Dimens.margin_small_x),
    )
    ExpeDropdownMenu(
      expanded = expanded,
      items = items,
      onDismissRequest = { expanded = false },
    )
  }
}

@Composable
private fun getTransactionTypeItems(
  commandProcessor: (ReportScreenCommand) -> Unit,
  transactionType: TransactionType,
): ImmutableList<DropdownMenuItem> = persistentListOf(
  DropdownMenuItem(
    text = stringResource(id = TransactionType.EXPENSE.labelResId),
    selected = transactionType == TransactionType.EXPENSE,
    onClick = { commandProcessor(SetTransactionTypeCommand(TransactionType.EXPENSE)) },
  ),
  DropdownMenuItem(
    text = stringResource(id = TransactionType.INCOME.labelResId),
    selected = transactionType == TransactionType.INCOME,
    onClick = { commandProcessor(SetTransactionTypeCommand(TransactionType.INCOME)) },
  ),
)

@Composable
internal fun CategoryItem(
  icon: ImageVector,
  tint: Color,
  amount: Amount,
  name: String,
  transactionType: TransactionType,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(Dimens.margin_large_x)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
      )
      Text(
        text = name,
        style = MaterialTheme.typography.bodyMedium,
        overflow = TextOverflow.Ellipsis,
      )
      AmountText(
        amount = amount,
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.End,
        transactionType = transactionType,
      )
    }
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DateRangePickerDialog(
  showDialogProvider: () -> Boolean,
  commandProcessor: (ReportScreenCommand) -> Unit,
) {
  val datePickerState = rememberDateRangePickerState()
  if (showDialogProvider()) {
    DatePickerDialog(
      onDismissRequest = { commandProcessor(HidePickerDialogCommand()) },
      confirmButton = {
        Button(
          onClick = {
            commandProcessor(
              SelectDateCommand(
                selectedStartDateMillis = datePickerState.selectedStartDateMillis,
                selectedEndDateMillis = datePickerState.selectedEndDateMillis,
              )
            )
          }
        ) {
          Text(text = stringResource(id = R.string.ok))
        }
      },
      dismissButton = {
        Button(onClick = { commandProcessor(HidePickerDialogCommand()) }) {
          Text(text = stringResource(id = R.string.cancel))
        }
      }
    ) {
      DateRangePicker(state = datePickerState)
    }
  }
}

@ExpePreview
@Composable
private fun ReportScreenPreview() {
  val pieChartData = persistentListOf(
    ReportPieChartSlice(1, 150f, ColorModel.random),
    ReportPieChartSlice(2, 120f, ColorModel.random),
    ReportPieChartSlice(3, 110f, ColorModel.random),
    ReportPieChartSlice(4, 170f, ColorModel.random),
    ReportPieChartSlice(5, 120f, ColorModel.random),
  )

  val screenData =
    ReportScreenData(
      balanceDate = textValueOf("Today"),
      balance = Amount.Mock,
      pieChartData = pieChartData,
      reportSum = ReportScreenData.ReportSum(textValueOf("All expenses"), Amount.Mock, TransactionType.EXPENSE),
      categoryValues = List(10) {
        ReportScreenData.CategoryValue(
          categoryId = it.toLong(),
          icon = IconModel.random,
          categoryName = textValueOf("Category $it"),
          amount = Amount.Mock,
          color = ColorModel.random,
        )
      }.toImmutableList(),
      transactionType = TransactionType.EXPENSE,
    )
  ExpensesTrackerTheme {
    Surface {
      ReportScreenContent(
        stateProvider = { NetworkViewState.Success(screenData) },
        periodsProvider = {
          persistentListOf(
            ReportPeriod.Custom(),
            ReportPeriod.AllTime(),
            ReportPeriod.Date(
              label = textValueOf("2024"),
              start = Instant.DISTANT_PAST,
              end = Instant.DISTANT_FUTURE,
            ),
            ReportPeriod.Date(
              label = textValueOf("2023"),
              start = Instant.DISTANT_PAST,
              end = Instant.DISTANT_FUTURE,
            ),
            ReportPeriod.Date(
              label = textValueOf("June '24"),
              start = Instant.DISTANT_PAST,
              end = Instant.DISTANT_FUTURE,
            ),
            ReportPeriod.Date(
              label = textValueOf("May '24"),
              start = Instant.DISTANT_PAST,
              end = Instant.DISTANT_FUTURE,
            ),
            ReportPeriod.Date(
              label = textValueOf("April '24"),
              start = Instant.DISTANT_PAST,

              end = Instant.DISTANT_FUTURE,
            ),
            ReportPeriod.Date(
              label = textValueOf("March '24"),
              start = Instant.DISTANT_PAST,
              end = Instant.DISTANT_FUTURE,
            ),
          )
        },
        selectedPeriodIndexProvider = { 2 },
        commandProcessor = {},
        showPickerDialogProvider = { false },
        selectedCategoryProvider = { null },
        selectedPeriodProvider = {
          ReportPeriod.Date(
            label = textValueOf("June '24"),
            start = Instant.DISTANT_PAST,
            end = Instant.DISTANT_FUTURE,
          )
        }
      )
    }
  }
}