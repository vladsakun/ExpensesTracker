package com.emendo.expensestracker.report

import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.common.NetworkViewState
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerSmallRadiusShape
import com.emendo.expensestracker.core.designsystem.utils.dpToPx
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.ui.AmountText
import com.emendo.expensestracker.core.ui.piechart.charts.DonutPieChart
import com.emendo.expensestracker.core.ui.piechart.models.PieChartConfig
import com.emendo.expensestracker.core.ui.piechart.models.PieChartData
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.data.api.model.transaction.labelResId
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color
import com.emendo.expensestracker.model.ui.textValueOf
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlin.math.roundToInt

@Destination(start = true)
@Composable
internal fun ReportScreen(
  navigator: DestinationsNavigator,
  viewModel: ReportViewModel = hiltViewModel(),
) {
  val state: State<NetworkViewState<ReportScreenData>> = viewModel.state.collectAsStateWithLifecycle()

  ReportScreenContent(
    stateProvider = state::value,
    commandProcessor = viewModel::proceedCommand,
  )
}

@Composable
private fun ReportScreenContent(
  stateProvider: () -> NetworkViewState<ReportScreenData>,
  commandProcessor: (ReportScreenCommand) -> Unit,
) {
  ExpeScaffoldWithTopBar(titleResId = R.string.report_title) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
    ) {
      when (val stateValue = stateProvider()) {
        is NetworkViewState.Error -> Text(text = stateValue.message)
        is NetworkViewState.Loading -> ExpLoadingWheel()
        is NetworkViewState.Success -> ReportScreenContent(
          state = stateValue.data,
          commandProcessor = commandProcessor,
        )
      }
    }
  }
}

@Composable
private fun ReportScreenContent(
  state: ReportScreenData,
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
      reportPeriods = state.periods,
      selectedPeriod = state.selectedPeriod,
      commandProcessor = commandProcessor,
    )
    balance(
      balanceDate = state.balanceDate,
      balance = state.balance,
      transactionType = state.transactionType,
    )
    pieChart(reportPieChartSlices = state.pieChartData)
    reportSum(state = state)
    categoryItems(
      categoryValues = state.categoryValues,
      transactionType = state.transactionType,
    )
  }
}

private fun LazyListScope.periods(
  reportPeriods: ImmutableList<ReportPeriod>,
  selectedPeriod: ReportPeriod,
  commandProcessor: (ReportScreenCommand) -> Unit,
) {
  uniqueItem("periods") {
    val indicatorOffset = remember { Animatable(0f) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val halfOfScreen = LocalConfiguration.current.screenWidthDp.dp.dpToPx() / 2
    val selectedItemWidth = remember(reportPeriods) { Animatable(0f) }
    val coordinates = remember(reportPeriods) { mutableStateMapOf<ReportPeriod, LayoutCoordinates>() }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(scrollState, reverseScrolling = true),
    ) {
      reportPeriods.reversed().forEach { period ->
        Column {
          Text(
            text = period.label.stringValue(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
              .onGloballyPositioned {
                coordinates[period] = it
              }
              .clickable(
                onClick = {
                  commandProcessor(SetPeriodCommand(period))
                  coroutineScope.launch {
                    val coordinate = coordinates[period]!!.positionInWindow()
                    val x = coordinate.x
                    val width = coordinates[period]!!.size.width.toFloat()
                    scrollState.animateScrollTo(x.roundToInt(), animationSpec = tween(200))
                    selectedItemWidth.animateTo(width)
                    indicatorOffset.animateTo(x)
                  }
                }
              )
              .padding(Dimens.margin_large_x)
              .fillMaxWidth(),
          )
        }
      }
    }

    Box(
      modifier = Modifier
        .height(2.dp)
        .width(with(LocalDensity.current) { selectedItemWidth.value.toDp() })
        .offset {
          // Use the animated offset as the offset of the Box.
          IntOffset(
            x = indicatorOffset.value.roundToInt(),
            y = 0,
          )
        }
        .clip(RoundedCornerSmallRadiusShape)
        .background(MaterialTheme.colorScheme.primary)
    )
    VerticalSpacer(height = Dimens.margin_large_x)
  }
}

private fun LazyListScope.categoryItems(
  categoryValues: ImmutableList<ReportScreenData.CategoryValue>, transactionType: TransactionType,
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
      onClick = {},
    )
    ExpeDivider()
  }
}

private fun LazyListScope.reportSum(state: ReportScreenData) {
  uniqueItem("reportSum") {
    CategoryItem(
      icon = ExpeIcons.Functions,
      tint = MaterialTheme.customColorsPalette.neutralColor,
      amount = state.allExpenses,
      name = state.reportSumLabel.stringValue(),
      transactionType = state.transactionType,
      onClick = {},
    )
    ExpeDivider()
    VerticalSpacer(height = Dimens.margin_large_x)
  }
}

private fun LazyListScope.pieChart(reportPieChartSlices: ImmutableList<ReportPieChartSlice>) {
  uniqueItem("pieChart") {
    val pieChartData = PieChartData(
      reportPieChartSlices.map {
        PieChartData.Slice(it.value, it.color.color)
      }.toImmutableList()
    )
    DonutPieChart(
      modifier = Modifier
        .padding(Dimens.margin_large_x)
        .fillMaxWidth()
        .aspectRatio(1f),
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
      onSliceClick = {},
    )
  }
}

private fun LazyListScope.balance(
  balanceDate: String,
  balance: Amount,
  transactionType: TransactionType,
) {
  uniqueItem("balance") {
    Row(
      horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = Dimens.margin_large_x),
    ) {
      Text(
        text = balanceDate,
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
private fun CategoryItem(
  icon: ImageVector,
  tint: Color,
  amount: Amount,
  name: String,
  transactionType: TransactionType,
  onClick: () -> Unit,
) {
  Box(
    modifier = Modifier
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

@ExpePreview
@Composable
private fun ReportScreenPreview() {
  val pieChartData = persistentListOf(
    ReportPieChartSlice(150f, ColorModel.random),
    ReportPieChartSlice(120f, ColorModel.random),
    ReportPieChartSlice(110f, ColorModel.random),
    ReportPieChartSlice(170f, ColorModel.random),
    ReportPieChartSlice(120f, ColorModel.random),
  )

  val screenData =
    ReportScreenData(
      balanceDate = "Today",
      balance = Amount.Mock,
      pieChartData = pieChartData,
      allExpenses = Amount.Mock,
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
      reportSumLabel = textValueOf("All expenses"),
      periods = persistentListOf(
        ReportPeriod.Custom(),
        ReportPeriod.AllTime(),
        ReportPeriod.Date(
          label = textValueOf("2024"),
          start = Instant.DISTANT_PAST,
          end = Instant.DISTANT_FUTURE,
          selected = true,
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
      ),
      selectedPeriod = ReportPeriod.Date(
        label = textValueOf("June '24"),
        start = Instant.DISTANT_PAST,
        end = Instant.DISTANT_FUTURE,
      ),
    )
  ExpensesTrackerTheme {
    Surface {
      ReportScreenContent(
        stateProvider = { NetworkViewState.Success(screenData) },
        commandProcessor = {},
      )
    }
  }
}