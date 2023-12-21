package com.emendo.expensestracker.createtransaction.transaction

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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

private val marginVertical = Dimens.margin_large_x
private val marginHorizontal = Dimens.margin_large_x
private const val ERROR_ANIMATION_DURATION_MILLIS = 500

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
      onSourceAmountClick = remember { { viewModel.showCalculatorBottomSheet() } },
      onTargetAmountClick = remember { { viewModel.showCalculatorBottomSheet(sourceTrigger = false) } },
      onCategoryClick = remember { { navigator.navigate(SelectCategoryScreenDestination) } },
      onAccountClick = remember { viewModel::openAccountListScreen },
      onTransferTargetAccountClick = remember { viewModel::selectTransferTargetAccount },
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

// Todo extract to a separate file
@Composable
fun Chevron(height: Dp, modifier: Modifier = Modifier) {
  val color = DividerDefaults.color
  Spacer(
    modifier = modifier
      .height(height)
      .drawWithCache {
        val path = androidx.compose.ui.graphics.Path()
        path.lineTo(size.width, size.height / 2f)
        path.lineTo(0f, size.height)
        onDrawBehind {
          drawPath(
            path = path,
            color = color,
            style = Stroke(width = Dimens.divider_thickness.toPx(), cap = StrokeCap.Round),
          )
        }
      }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTransactionContent(
  stateProvider: () -> CreateTransactionUiState,
  onSourceAmountClick: () -> Unit,
  onTargetAmountClick: () -> Unit,
  onCategoryClick: () -> Unit,
  onAccountClick: () -> Unit,
  onTransferTargetAccountClick: () -> Unit,
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
            // Todo improve transaction type switch animation
            TextSwitch(
              selectedIndex = transactionType.ordinal,
              items = tabsResId.map { stringResource(id = it) }.toPersistentList(),
              onSelectionChange = { tabIndex ->
                onTransactionTypeChange(tabIndex.toTransactionType())
              },
            )
          }
        },
        navigationIcon = { NavigationBackIcon(onNavigationClick = onBackPressed) },
        actions = persistentListOf(
          MenuAction(
            icon = ExpeIcons.Check,
            onClick = onCreateTransactionClick,
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
        is CreateTransactionUiState.DisplayTransactionData -> {
          uniqueItem("screen") {
            // Todo extract navigate up to a separate flow
            NavigationEventEffect(
              event = state.screenData.navigateUp,
              onConsumed = onConsumedNavigateUpEvent,
              action = onBackPressed,
            )
            if (state.screenData.transactionType == TransactionType.TRANSFER) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .defaultMinSize(minHeight = 80.dp)
                  .padding(horizontal = marginHorizontal),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(marginVertical),
              ) {
                Column(
                  modifier = Modifier
                    .weight(1f)
                    .padding(vertical = marginVertical)
                    .defaultMinSize(minHeight = 80.dp),
                  verticalArrangement = Arrangement.Center,
                ) {
                  val source = state.source
                  if (source == null) {
                    Text(
                      text = stringResource(id = R.string.create_account),
                      modifier = Modifier.clickable(onClick = onAccountClick),
                    )
                  } else {
                    TransferAccount(onAccountClick, source)
                    TransferAmount(
                      text = state.screenData.amount.formattedValue,
                      focused = state.sourceAmountFocused,
                      onClick = onSourceAmountClick,
                    )
                  }
                }
                Chevron(height = 90.dp, modifier = Modifier.width(12.dp))
                Column(
                  modifier = Modifier
                    .weight(1f)
                    .padding(vertical = marginVertical)
                    .defaultMinSize(minHeight = 80.dp),
                  verticalArrangement = Arrangement.Center,
                ) {
                  val target = state.target
                  if (target == null) {
                    Text(
                      text = stringResource(id = R.string.create_account),
                      modifier = Modifier.clickable(onClick = onAccountClick),
                    )
                  } else {
                    TransferAccount(onTransferTargetAccountClick, target)
                    state.transferReceivedAmount?.let { amount ->
                      TransferAmount(
                        text = amount.formattedValue,
                        focused = state.transferTargetAmountFocused,
                        textColor = if (state.isCustomTransferAmount) MaterialTheme.customColorsPalette.successColor else MaterialTheme.colorScheme.outline,
                        onClick = onTargetAmountClick,
                      )
                    }
                  }
                }
              }
            } else {
              Amount(
                onClick = onSourceAmountClick,
                text = state.screenData.amount.formattedValue,
                transactionType = state.screenData.transactionType,
                error = state.screenData.amountError == triggered,
                onErrorConsumed = { onErrorConsumed(FieldWithError.Amount) },
                focused = state.sourceAmountFocused,
              )
            }
            ExpeDivider()
            if (state.screenData.transactionType != TransactionType.TRANSFER) {
              TransactionElementRow(
                transactionItem = state.target,
                label = stringResource(id = R.string.category),
                onClick = onCategoryClick,
              )
              TransactionElementRow(
                transactionItem = state.source,
                label = stringResource(id = R.string.account),
                onClick = onAccountClick,
                error = state.screenData.sourceError == triggered,
                onErrorConsumed = remember { { onErrorConsumed(FieldWithError.Source) } },
              )
              ExpeDivider()
            }
            ExpeTextField(
              text = state.note,
              onValueChange = onNoteValueChange,
              modifier = Modifier.fillMaxWidth(),
              placeholder = stringResource(id = R.string.create_transaction_note_placeholder),
              paddingValues = PaddingValues(horizontal = marginHorizontal, vertical = marginVertical),
            )
            ExpeDivider()
            VerticalSpacer(marginVertical)
            ExpeButton(
              textResId = R.string.save_transaction,
              onClick = onCreateTransactionClick,
              modifier = Modifier
                .padding(horizontal = marginHorizontal)
            )
            VerticalSpacer(marginVertical)
            Column {
              CreateTransactionRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(marginVertical),
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
}

@Composable
private fun TransferAmount(
  text: String,
  focused: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  textColor: Color = MaterialTheme.typography.headlineMedium.color,
) {
  AutoSizableTextField(
    text = text,
    focused = focused,
    modifier = modifier.clickable(onClick = onClick),
    minFontSize = MaterialTheme.typography.bodyMedium.fontSize,
    style = MaterialTheme.typography.headlineMedium,
    color = textColor,
    textAlign = TextAlign.End,
    maxLines = 1,
  )
}

@Composable
private fun TransferAccount(
  onClick: () -> Unit,
  account: TransactionItemModel,
) {
  Row(
    modifier = Modifier
      .heightIn(min = Dimens.icon_button_size)
      .clickable(
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
      )
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.End,
  ) {
    Icon(
      modifier = Modifier.size(Dimens.icon_size),
      imageVector = account.icon.imageVector,
      contentDescription = null,
    )
    Text(text = account.name.stringValue(), style = MaterialTheme.typography.bodyLarge)
  }
}

@Composable
private fun RowScope.AdditionalAction(
  @StringRes titleResId: Int,
  icon: ImageVector,
  onClick: () -> Unit,
  enabled: Boolean = true,
) {
  ExpeButtonWithIcon(
    titleResId = titleResId,
    icon = icon,
    onClick = onClick,
    enabled = enabled,
    modifier = Modifier
      .weight(1f)
      .heightIn(min = Dimens.icon_button_size),
  )
}

@Composable
private fun Amount(
  text: String,
  transactionType: TransactionType,
  error: Boolean,
  onClick: () -> Unit,
  onErrorConsumed: () -> Unit,
  focused: Boolean,
  modifier: Modifier = Modifier,
) {
  val backgroundColor = animateColorAsState(
    targetValue = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
    finishedListener = { onErrorConsumed() },
    animationSpec = tween(ERROR_ANIMATION_DURATION_MILLIS),
    label = "error"
  )
  AutoSizableTextField(
    text = text,
    minFontSize = MaterialTheme.typography.bodyMedium.fontSize,
    color = transactionType.amountColor(),
    style = MaterialTheme.typography.headlineMedium,
    textAlign = TextAlign.End,
    focused = focused,
    maxLines = 1,
    modifier = modifier
      .fillMaxWidth()
      .drawBehind { drawRect(backgroundColor.value) }
      .clickable(onClick = onClick)
      .padding(
        vertical = marginVertical,
        horizontal = marginHorizontal,
      ),
  )
}

@Composable
private fun ColumnScope.BottomSheetContent(sheetData: BottomSheetData) {
  // Todo rethink ModalBottomSheet. It would be nice to switch transaction type (to Transfer) without closing the keyboard
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
private fun TransactionElementRow(
  transactionItem: TransactionItemModel?,
  label: String,
  onClick: () -> Unit,
  error: Boolean = false,
  modifier: Modifier = Modifier,
  onErrorConsumed: () -> Unit = {},
) {
  val backgroundColor = animateColorAsState(
    targetValue = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
    finishedListener = { onErrorConsumed() },
    animationSpec = tween(ERROR_ANIMATION_DURATION_MILLIS),
    label = "error"
  )
  CreateTransactionRow(
    modifier = modifier
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
      TransactionElement(
        icon = model.icon.imageVector,
        title = model.name.stringValue(),
        tint = model.color.color,
      )
    }
  }
  ExpeDivider()
}

@Composable
private fun TransactionElement(
  icon: ImageVector,
  title: String,
  tint: Color,
) {
  Icon(
    imageVector = icon,
    contentDescription = null,
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
    modifier = modifier.padding(vertical = marginVertical, horizontal = marginHorizontal),
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