package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.app.resources.models.ColorModel.Companion.color
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import com.emendo.expensestracker.core.data.model.transaction.TransactionType.Companion.toTransactionType
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseScreenWithModalBottomSheetWithViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.TransactionCalculatorBottomSheet
import com.emendo.expensestracker.core.ui.loader
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.createtransaction.destinations.SelectAccountScreenDestination
import com.emendo.expensestracker.createtransaction.destinations.SelectCategoryScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import de.palm.composestateevents.NavigationEventEffect
import de.palm.composestateevents.triggered
import kotlinx.collections.immutable.persistentListOf
import com.emendo.expensestracker.core.app.resources.R as AppR

val marginHorizontal = Dimens.margin_large_x

@RootNavGraph(start = true)
@Destination(
  //  style = BottomSheetTransition::class,
  start = true
)
@Composable
fun CreateTransactionScreen(
  navigator: DestinationsNavigator,
  viewModel: CreateTransactionViewModel = hiltViewModel(),
) {
  val uiState = viewModel.uiState.collectAsStateWithLifecycle()

  BaseScreenWithModalBottomSheetWithViewModel(
    viewModel = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    bottomSheetContent = { type, hideBottomSheet -> BottomSheetContent(type, hideBottomSheet) }
  ) {
    CreateTransactionContent(
      stateProvider = uiState::value,
      onAmountClick = remember { viewModel::showCalculatorBottomSheet },
      onCategoryClick = remember { { navigator.navigate(SelectCategoryScreenDestination) } },
      onAccountClick = remember { { navigator.navigate(SelectAccountScreenDestination) } },
      onCreateTransactionClick = remember { viewModel::saveTransaction },
      onBackPressed = navigator::navigateUp,
      onErrorConsumed = remember { viewModel::consumeFieldError },
      onConsumedNavigateUpEvent = remember { viewModel::consumeCloseEvent },
      onTransactionTypeChange = remember { viewModel::changeTransactionType },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTransactionContent(
  stateProvider: () -> CreateTransactionUiState,
  onAmountClick: () -> Unit,
  onCategoryClick: () -> Unit,
  onAccountClick: () -> Unit,
  onCreateTransactionClick: () -> Unit,
  onBackPressed: () -> Unit,
  onErrorConsumed: (field: FieldWithError) -> Unit,
  onConsumedNavigateUpEvent: () -> Unit,
  onTransactionTypeChange: (TransactionType) -> Unit,
) {
  val selectedTabState = rememberSaveable { mutableIntStateOf(TransactionType.DEFAULT.ordinal) }
  ExpeScaffold(
    topBar = {
      ExpeMediumTopBar(
        title = {
          val tabs = persistentListOf("Income", "Expense", "Transfer")
          TextSwitch(
            selectedIndex = selectedTabState.intValue,
            items = tabs,
            onSelectionChange = { tabIndex ->
              selectedTabState.intValue = tabIndex
              onTransactionTypeChange(tabIndex.toTransactionType())
            },
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackPressed) {
            Icon(
              imageVector = ExpeIcons.Close,
              contentDescription = stringResource(id = AppR.string.back),
              tint = MaterialTheme.colorScheme.onSurface,
            )
          }
        },
        actions = persistentListOf(
          MenuAction(
            icon = ExpeIcons.Check,
            onClick = {},
            text = stringResource(id = AppR.string.confirm),
          )
        ),
      )
    },
    modifier = Modifier.fillMaxSize(),
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      LazyColumn {
        when (val state = stateProvider()) {
          is CreateTransactionUiState.Empty -> Unit
          is CreateTransactionUiState.Error -> error(state)
          is CreateTransactionUiState.Loading -> loader()
          is CreateTransactionUiState.DisplayTransactionData -> uniqueItem("screen") {
            NavigationEventEffect(
              event = state.screenData.navigateUp,
              onConsumed = onConsumedNavigateUpEvent,
              action = onBackPressed,
            )
            Text(
              text = state.screenData.amount,
              color = state.screenData.transactionType.amountColor(),
              style = MaterialTheme.typography.headlineMedium,
              textAlign = TextAlign.End,
              modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onAmountClick)
                .padding(
                  vertical = Dimens.margin_large_x,
                  horizontal = marginHorizontal,
                ),
            )
            ExpeDivider()
            TransactionDestinationRow(
              transactionItem = state.target,
              label = stringResource(id = AppR.string.category),
              onClick = onCategoryClick,
              error = false,
            )
            TransactionDestinationRow(
              transactionItem = state.source,
              label = stringResource(id = AppR.string.account),
              onClick = onAccountClick,
              error = state.screenData.sourceError == triggered,
              onConsumed = { onErrorConsumed(FieldWithError.Source) },
            )
            ExpeButton(
              textResId = AppR.string.save_transaction,
              onClick = onCreateTransactionClick,
              modifier = Modifier.padding(horizontal = marginHorizontal),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun BottomSheetContent(
  type: BottomSheetType?,
  hideBottomSheet: () -> Unit,
) {
  when (type) {
    is BottomSheetType.Calculator -> {
      val state = type.state.collectAsStateWithLifecycle()

      TransactionCalculatorBottomSheet(
        textStateProvider = { state.value.text },
        currencyState = { state.value.currency },
        equalButtonState = { state.value.equalButtonState },
        decimalSeparator = type.decimalSeparator,
        calculatorActions = type.actions,
        numericKeyboardActions = type.numericKeyboardActions,
      )
    }

    else -> {
      Unit
    }
  }
}

@Composable
private fun TransactionDestinationRow(
  transactionItem: TransactionItemModel?,
  label: String,
  onClick: () -> Unit,
  error: Boolean,
  onConsumed: () -> Unit = {},
) {
  val errorFinished: (Color) -> Unit = {
    onConsumed()
  }
  val backgroundColor = animateColorAsState(
    targetValue = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
    finishedListener = errorFinished,
    animationSpec = tween(500),
    label = "error"
  )
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clickable(onClick = onClick)
      .background(backgroundColor.value)
      .padding(
        vertical = Dimens.margin_large_x,
        horizontal = marginHorizontal
      ),
  ) {
    Text(
      text = label,
      color = MaterialTheme.colorScheme.secondary,
    )
    Spacer(modifier = Modifier.width(Dimens.margin_small_x))
    Spacer(modifier = Modifier.weight(1f))
    transactionItem?.let { model ->
      TransactionItem(
        icon = model.icon.imageVector,
        title = model.name.stringValue(),
        tint = model.color.color,
      )
    }
  }
  ExpeDivider()
}

@Composable
private fun TransactionItem(
  icon: ImageVector,
  title: String,
  tint: Color,
) {
  Icon(
    imageVector = icon,
    contentDescription = "Transaction item icon",
    tint = tint,
  )
  Spacer(modifier = Modifier.width(Dimens.margin_small_xx))
  Text(
    text = title,
    textAlign = TextAlign.End,
    overflow = TextOverflow.Ellipsis,
    maxLines = 1,
  )
}

private fun LazyListScope.error(state: CreateTransactionUiState.Error) {
  uniqueItem("error") { Text(text = state.message) }
}

@Composable
@ReadOnlyComposable
fun TransactionType.amountColor(): Color {
  if (this == TransactionType.INCOME) {
    return MaterialTheme.customColorsPalette.successColor
  }

  return LocalTextStyle.current.color
}