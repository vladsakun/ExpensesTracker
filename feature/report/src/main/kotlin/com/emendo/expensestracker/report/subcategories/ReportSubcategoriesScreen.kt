package com.emendo.expensestracker.report.subcategories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aay.compose.barChart.model.BarParameters
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
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.config.BarChartColorConfig
import com.himanshoe.charty.bar.config.BarChartConfig
import com.himanshoe.charty.bar.config.BarTooltip
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.common.LabelConfig
import com.himanshoe.charty.common.asSolidChartColor
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
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
        is NetworkViewState.Idle -> Unit
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
    //    barChart3(state.barData, color = state.color)
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

private fun LazyListScope.barChart(data: ImmutableList<BarData>, color: ColorModel) {
  uniqueItem("barChart") {
    BarChart(
      modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()
        .height(100.dp),
      barTooltip = BarTooltip.BarTop,
      labelConfig = LabelConfig.default().copy(
        showYLabel = true,
        xAxisCharCount = 0,
        showXLabel = false,
        textColor = MaterialTheme.colorScheme.onSurface.asSolidChartColor(),
      ),
      barChartColorConfig = BarChartColorConfig.default().copy(fillBarColor = color.color.asSolidChartColor()),
      data = { data },
      barChartConfig = BarChartConfig.default().copy(
        cornerRadius = CornerRadius(40F, 40F),
        showAxisLines = false,
        showGridLines = true,
      ),
      onBarClick = { index, barData -> println("click in bar with $index index and data $barData") })
  }
}

private fun LazyListScope.barChart2(data: ImmutableList<BarParameters>, color: ColorModel) {
  uniqueItem("barChart") {
    com.aay.compose.barChart.BarChart(
      chartParameters = data,
      gridColor = Color.DarkGray,
      xAxisData = data.first().data.map { "test" },
      isShowGrid = true,
      animateChart = true,
      showGridWithSpacer = true,
      yAxisStyle = TextStyle(
        fontSize = 14.sp,
        color = Color.DarkGray,
      ),
      xAxisStyle = TextStyle(
        fontSize = 14.sp,
        color = Color.DarkGray,
        fontWeight = FontWeight.W400
      ),
      yAxisRange = 15,
      barWidth = 20.dp
    )
  }
}

private fun LazyListScope.barChart3(data: ImmutableList<BarData>, color: ColorModel) {
  uniqueItem("barChart") {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
      modelProducer.runTransaction { columnSeries { series(data.map { it.yValue }) } }
    }

    val positiveColumn =
      rememberLineComponent(
        fill = fill(color.color),
        thickness = 8.dp,
        shape = CorneredShape.rounded(
          topLeftDp = Dimens.corner_radius_normal.value,
          topRightDp = Dimens.corner_radius_normal.value
        ),
      )

    val scrollState = rememberVicoScrollState(false)
    CartesianChartHost(
      chart = rememberCartesianChart(
        rememberColumnCartesianLayer(columnProvider = remember(positiveColumn) { getColumnProvider(positiveColumn) }),
        startAxis = VerticalAxis.rememberStart(),
        bottomAxis = HorizontalAxis.rememberBottom(
          guideline = null,
        ),
      ),
      modelProducer = modelProducer,
      scrollState = scrollState,
    )
  }
}

private fun getColumnProvider(positive: LineComponent) =
  object : ColumnCartesianLayer.ColumnProvider {
    override fun getColumn(
      entry: ColumnCartesianLayerModel.Entry,
      seriesIndex: Int,
      extraStore: ExtraStore,
    ) = positive

    override fun getWidestSeriesColumn(seriesIndex: Int, extraStore: ExtraStore) = positive
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