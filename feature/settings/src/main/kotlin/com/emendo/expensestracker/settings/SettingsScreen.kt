package com.emendo.expensestracker.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.component.ExpePreview
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.ui.piechart.charts.DonutPieChart
import com.emendo.expensestracker.core.ui.piechart.models.PieChartConfig
import com.emendo.expensestracker.core.ui.piechart.models.PieChartData
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.persistentListOf

const val NO_SELECTED_SLICE = -1

@Destination(start = true)
@Composable
internal fun SettingsRoute(
  navigator: DestinationsNavigator,
  viewModel: SettingsViewModel = hiltViewModel(),
) {
  val uiState = viewModel.state.collectAsStateWithLifecycle()

  ExpeScaffoldWithTopBar(titleResId = R.string.settings) {
    SettingsScreen(
      uiStateProvider = uiState::value,
      onItemClick = viewModel::onItemClick,
      modifier = Modifier
        .fillMaxSize()
        .padding(it),
    )
  }
}

@Composable
private fun SettingsScreen(
  uiStateProvider: () -> SettingsScreenData,
  onItemClick: (SettingsItemModel) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    DonutPieChart(
      modifier = Modifier.fillMaxSize(),
      pieChartData = PieChartData(
        slices = listOf(
          PieChartData.Slice("Sample-1", 150f, Color.Red),
          PieChartData.Slice("Sample-2", 120f, Color.Blue),
          PieChartData.Slice("Sample-3", 110f, Color.Green),
          PieChartData.Slice("Sample-4", 170f, Color.Black),
          PieChartData.Slice("Sample-5", 120f, Color.Magenta),
        )
      ),
      pieChartConfig = PieChartConfig(
        isAnimationEnable = true,
        chartPadding = 50,
        strokeWidth = 250f,
        sliceLabelTextSize = 12.sp,
      ),
      onSliceClick = {}
    )
  }
  //  LazyColumn(
  //    contentPadding = PaddingValues(vertical = Dimens.margin_large_x),
  //    modifier = modifier.fillMaxSize(),
  //  ) {
  //    items(
  //      items = uiStateProvider().settingsItems,
  //      key = { it.id },
  //      contentType = { "settingsItem" },
  //    ) {
  //      SettingsItem(
  //        item = it,
  //        onClick = { onItemClick(it) },
  //      )
  //    }
  //  }
}

@Composable
fun PieChart(
  data: PieChartData,
  modifier: Modifier = Modifier,
  //  pieChartData: PieChartData,
  radiusOuter: Dp = 140.dp,
  chartBarWidth: Dp = 40.dp,
  animDuration: Int = 1000,
) {

  //  val totalSum = data.values.sumOf { it.toDouble() }
  //  val floatValue = mutableListOf<Float>()
  //
  //  data.values.forEachIndexed { index, bigDecimal ->
  //    val value = 360 * bigDecimal.toDouble() / totalSum
  //    floatValue.add(index, value.toFloat() + 1)
  //  }
  //  var animationPlayed by rememberSaveable { mutableStateOf(false) }
  //
  //  var lastValue = 0f
  //
  //  // it is the diameter value of the Pie
  //  val animateSize by animateFloatAsState(
  //    targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
  //    animationSpec = tween(
  //      durationMillis = animDuration,
  //      delayMillis = 0,
  //      easing = LinearOutSlowInEasing
  //    )
  //  )
  //
  //  // if you want to stabilize the Pie Chart you can use value -90f
  //  // 90f is used to complete 1/4 of the rotation
  //  val animateRotation by animateFloatAsState(
  //    targetValue = if (animationPlayed) 90f * 11f else 0f,
  //    animationSpec = tween(
  //      durationMillis = animDuration,
  //      delayMillis = 0,
  //      easing = LinearOutSlowInEasing
  //    )
  //  )
  //
  //  // to play the animation only once when the function is Created or Recomposed
  //  LaunchedEffect(Unit) {
  //    animationPlayed = true
  //  }
  //  val primary = MaterialTheme.colorScheme.primary
  //  val secondary = MaterialTheme.colorScheme.secondary
  //  val context = LocalContext.current
  //  val onClick: (index: Int) -> Unit = {
  //    Toast.makeText(context, "Hello $it", Toast.LENGTH_SHORT).show()
  //  }
  //  // Sum of all the values
  //  val sumOfValues = data.totalLength
  //
  //  // Calculate each proportion value
  //  val proportions = data.slices.proportion(sumOfValues)
  //
  //  // Convert each proportions to angle
  //  val sweepAngles = proportions.sweepAngles()
  //
  //  val progressSize = mutableListOf<Float>()
  //  progressSize.add(sweepAngles.first())
  //
  //  for (x in 1 until sweepAngles.size) {
  //    progressSize.add(sweepAngles[x] + progressSize[x - 1])
  //  }
  //
  //  Column(
  //    modifier = Modifier.fillMaxWidth(),
  //    horizontalAlignment = Alignment.CenterHorizontally
  //  ) {
  //
  //    // Pie Chart using Canvas Arc
  //    BoxWithConstraints(
  //      modifier = Modifier.size(animateSize.dp),
  //      contentAlignment = Alignment.Center
  //    ) {
  //      val sideSize = Integer.min(constraints.maxWidth, constraints.maxHeight)
  //      Canvas(
  //        modifier = Modifier
  //          .size(radiusOuter * 2f)
  //          //          .rotate(animateRotation)
  //          .pointerInput(true) {
  //            detectTapGestures {
  //              val clickedAngle = convertTouchEventPointToAngle(
  //                sideSize.toFloat(),
  //                sideSize.toFloat(),
  //                it.x,
  //                it.y
  //              )
  //              progressSize.forEachIndexed { index, item ->
  //                if (clickedAngle <= item) {
  //                  //                  activePie = if (activePie != index)
  //                  //                    index
  //                  //                  else
  //                  //                    NO_SELECTED_SLICE
  //                  onClick(index)
  //                  return@detectTapGestures
  //                }
  //              }
  //            }
  //          }
  //      ) {
  //        // draw each Arc for each data entry in Pie Chart
  //        sweepAngles.forEachIndexed { index, value ->
  //          val color = if (index % 2 == 0) primary else secondary
  //          drawArc(
  //            color = color,
  //            startAngle = lastValue,
  //            sweepAngle = value + 1, // add 1 to overlap arcs a bit
  //            useCenter = false,
  //            style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt),
  //          )
  //
  //          lastValue += value
  //        }
  //      }
  //    }
  //  }
}

@Composable
private fun SettingsItem(
  item: SettingsItemModel,
  onClick: () -> Unit,
) {
  Column {
    Row(
      modifier = Modifier
        .clickable(onClick = onClick)
        .padding(Dimens.margin_large_x),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x)
    ) {
      Icon(
        imageVector = item.icon,
        contentDescription = null,
      )
      Text(
        text = stringResource(id = item.titleResId),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
          .weight(1f)
          .padding(end = Dimens.margin_small_x),
      )
      item.value?.let {
        Text(
          text = it.text,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.secondary,
        )
      }
    }
    ExpeDivider(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = Dimens.icon_size + Dimens.margin_large_x * 2, end = Dimens.margin_large_x)
    )
  }
}

private val SettingsItemValue.text: String
  @Composable
  @ReadOnlyComposable
  get() = when (this) {
    is SettingsItemValue.StringValue -> value
    is SettingsItemValue.StringResValue -> stringResource(id = resId)
  }

@ExpePreview
@Composable
private fun SettingsScreenPreview() {
  ExpensesTrackerTheme {
    SettingsScreen(
      uiStateProvider = { SettingsScreenData(settingsItems = persistentListOf()) },
      onItemClick = {},
    )
  }
}