package com.emendo.expensestracker.createtransaction.transaction

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.app.resources.models.ColorModel.Companion.color
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import com.emendo.expensestracker.core.data.model.transaction.TransactionType.Companion.toTransactionType
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.PlaceholderTextStyle
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusShape
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.emendo.expensestracker.core.ui.bottomsheet.BottomScreenTransition
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ScreenWithModalBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.TransactionCalculatorBottomSheet
import com.emendo.expensestracker.core.ui.loader
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.createtransaction.destinations.SelectCategoryScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import de.palm.composestateevents.NavigationEventEffect
import de.palm.composestateevents.triggered
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

val marginHorizontal = Dimens.margin_large_x

@RootNavGraph(start = true)
@Destination(
  style = BottomScreenTransition::class,
)
@Composable
fun CreateTransactionScreen(
  navigator: DestinationsNavigator,
  viewModel: CreateTransactionViewModel = hiltViewModel(),
) {
  val uiState = viewModel.uiState.collectAsStateWithLifecycle()

  ScreenWithModalBottomSheet(
    stateManager = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    bottomSheetContent = { type -> BottomSheetContent(type) }
  ) {
    CreateTransactionContent(
      stateProvider = uiState::value,
      onAmountClick = remember { viewModel::showCalculatorBottomSheet },
      onCategoryClick = remember { { navigator.navigate(SelectCategoryScreenDestination) } },
      onAccountClick = remember { viewModel::openAccountListScreen },
      onCreateTransactionClick = remember { viewModel::saveTransaction },
      onBackPressed = remember { { navigator.navigateUp() } },
      onErrorConsumed = remember { viewModel::consumeFieldError },
      onConsumedNavigateUpEvent = remember { viewModel::consumeCloseEvent },
      onTransactionTypeChange = remember { viewModel::changeTransactionType },
      onNoteValueChange = remember { viewModel::updateNoteText },
      onDeleteClick = remember { viewModel::showConfirmDeleteTransactionBottomSheet },
      onDuplicateClick = remember { viewModel::duplicateTransaction },
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
  onNoteValueChange: (String) -> Unit,
  onDeleteClick: () -> Unit,
  onDuplicateClick: () -> Unit,
) {
  ExpeScaffold(
    topBar = {
      ExpeCenterAlignedTopBar(
        title = {
          val transactionType = stateProvider().successValue?.screenData?.transactionType
          if (transactionType != null) {
            val tabsResId = listOf(R.string.income, R.string.expense, R.string.transfer)
            TextSwitch(
              selectedIndex = transactionType.ordinal,
              items = tabsResId.map { stringResource(id = it) }.toPersistentList(),
              onSelectionChange = { tabIndex ->
                onTransactionTypeChange(tabIndex.toTransactionType())
              },
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBackPressed) {
            Icon(
              imageVector = ExpeIcons.Close,
              contentDescription = stringResource(id = R.string.back),
              tint = MaterialTheme.colorScheme.onSurface,
            )
          }
        },
        actions = persistentListOf(
          MenuAction(
            icon = ExpeIcons.Check,
            onClick = {},
            contentDescription = stringResource(id = R.string.confirm),
          )
        ),
      )
    },
    modifier = Modifier.fillMaxSize(),
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when (val state = stateProvider()) {
        is CreateTransactionUiState.Loading -> loader()
        is CreateTransactionUiState.DisplayTransactionData -> uniqueItem("screen") {
          NavigationEventEffect(
            event = state.screenData.navigateUp,
            onConsumed = onConsumedNavigateUpEvent,
            action = onBackPressed,
          )
          Amount(
            onClick = onAmountClick,
            text = state.screenData.amount,
            transactionType = state.screenData.transactionType,
            error = state.screenData.amountError == triggered,
            onErrorConsumed = { onErrorConsumed(FieldWithError.Amount) },
          )
          ExpeDivider()
          TransactionDestinationRow(
            transactionItem = state.target,
            label = stringResource(id = R.string.category),
            onClick = onCategoryClick,
            error = false,
          )
          TransactionDestinationRow(
            transactionItem = state.source,
            label = stringResource(id = R.string.account),
            onClick = onAccountClick,
            error = state.screenData.sourceError == triggered,
            onErrorConsumed = { onErrorConsumed(FieldWithError.Source) },
          )
          CreateTransactionRow {
            ExpeTextField(
              placeholder = stringResource(id = R.string.create_transaction_note_placeholder),
              text = state.note,
              onValueChange = onNoteValueChange,
              modifier = Modifier.fillMaxWidth(),
            )
          }
          ExpeDivider()
          Spacer(modifier = Modifier.height(Dimens.margin_large_x))
          ExpeButton(
            textResId = R.string.save_transaction,
            onClick = onCreateTransactionClick,
            modifier = Modifier.padding(horizontal = marginHorizontal),
          )
          VerticalSpacer(height = Dimens.margin_large_x)
          Column {
            CreateTransactionRow(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
            ) {
              AdditionalAction(
                titleResId = R.string.delete,
                icon = ExpeIcons.Delete,
                onClick = onDeleteClick,
              )
              AdditionalAction(
                titleResId = R.string.duplicate,
                icon = ExpeIcons.FileCopy,
                onClick = onDuplicateClick,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun RowScope.AdditionalAction(
  @StringRes titleResId: Int,
  icon: ImageVector,
  onClick: () -> Unit,
  enabled: Boolean = true,
) {
  Button(
    onClick = onClick,
    modifier = Modifier.Companion
      .weight(1f)
      .heightIn(min = Dimens.icon_button_size),
    shape = RoundedCornerNormalRadiusShape,
    enabled = enabled,
  ) {
    Icon(imageVector = icon, contentDescription = stringResource(id = titleResId))
    HorizontalSpacer(Dimens.margin_small_x)
    Text(text = stringResource(id = titleResId), style = MaterialTheme.typography.labelLarge)
  }
}

@Composable
private fun Amount(
  text: String,
  transactionType: TransactionType,
  error: Boolean,
  onClick: () -> Unit,
  onErrorConsumed: () -> Unit,
) {
  val backgroundColor = animateColorAsState(
    targetValue = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
    finishedListener = { onErrorConsumed() },
    animationSpec = tween(500),
    label = "error"
  )
  Text(
    text = text,
    color = transactionType.amountColor(),
    style = MaterialTheme.typography.headlineMedium,
    textAlign = TextAlign.End,
    modifier = Modifier
      .fillMaxWidth()
      .drawBehind { drawRect(backgroundColor.value) }
      .clickable(onClick = onClick)
      .padding(
        vertical = Dimens.margin_large_x,
        horizontal = marginHorizontal,
      ),
  )
}

@Composable
private fun ColumnScope.BottomSheetContent(sheetData: BottomSheetData) {
  when (sheetData) {
    is CalculatorBottomSheetData -> {
      val state = sheetData.state.collectAsStateWithLifecycle()

      TransactionCalculatorBottomSheet(
        stateProvider = state::value,
        decimalSeparator = sheetData.decimalSeparator,
        calculatorActions = sheetData.actions,
        numericKeyboardActions = sheetData.numericKeyboardActions,
      )
    }

    is GeneralBottomSheetData -> GeneralBottomSheet(sheetData)
  }
}

@Composable
private fun TransactionDestinationRow(
  transactionItem: TransactionItemModel?,
  label: String,
  onClick: () -> Unit,
  error: Boolean,
  onErrorConsumed: () -> Unit = {},
) {
  val backgroundColor = animateColorAsState(
    targetValue = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
    finishedListener = { onErrorConsumed() },
    animationSpec = tween(500),
    label = "error"
  )
  CreateTransactionRow(
    modifier = Modifier
      .clickable(onClick = onClick)
      .drawBehind { drawRect(backgroundColor.value) },
  ) {
    Text(
      text = label,
      style = PlaceholderTextStyle,
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

@Composable
private fun CreateTransactionRow(
  modifier: Modifier = Modifier,
  horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
  content: @Composable RowScope.() -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = horizontalArrangement,
    modifier = modifier.padding(vertical = Dimens.margin_large_x, horizontal = marginHorizontal),
    content = content,
  )
}

@Composable
@ReadOnlyComposable
fun TransactionType.amountColor(): Color {
  if (this == TransactionType.INCOME) {
    return MaterialTheme.customColorsPalette.successColor
  }

  return LocalTextStyle.current.color
}