package com.emendo.expensestracker.transactions.list

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerSmallRadiusShape
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ScreenWithModalBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color
import com.emendo.expensestracker.model.ui.NetworkViewState
import com.emendo.expensestracker.model.ui.successData
import com.emendo.expensestracker.transactions.TransactionsListArgs
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.persistentListOf
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.time.toJavaInstant

@Destination(start = true)
@Composable
fun TransactionsListRoute(
  navigator: DestinationsNavigator,
  args: TransactionsListArgs? = null,
  viewModel: TransactionsListViewModel = hiltViewModel(),
) {
  val state = viewModel.state.collectAsStateWithLifecycle()

  ScreenWithModalBottomSheet(
    stateManager = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    bottomSheetContent = { type -> BottomSheetContent(type) },
  ) {
    TransactionsListScreenContent(
      stateProvider = state::value,
      onTransactionClick = { editMode, transaction ->
        navigator.navigate(viewModel.getTransactionDetailsRoute(transaction, editMode))
      },
      onCreateTransactionClick = { navigator.navigate(viewModel.getCreateTransactionRoute()) },
      backButton = args != null,
      onBackClick = navigator::navigateUp,
      commandProcessor = viewModel::proceedCommand,
    )
  }
}

@Composable
private fun TransactionsListScreenContent(
  stateProvider: () -> NetworkViewState<TransactionsUiState>,
  onTransactionClick: (Boolean, TransactionModel) -> Unit,
  onCreateTransactionClick: () -> Unit,
  backButton: Boolean,
  commandProcessor: (TransactionListCommand) -> Unit,
  onBackClick: () -> Unit,
) {
  ExpeScaffoldWithTopBar(
    titleResId = R.string.transactions,
    onNavigationClick = { onBackClick() }.takeIf { backButton },
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
    ) {
      when (val state = stateProvider()) {
        is NetworkViewState.Idle -> Unit
        is NetworkViewState.Loading -> ExpLoadingWheel()
        is NetworkViewState.Error -> Text(text = state.message.stringValue())
        is NetworkViewState.Success<*> -> TransactionsList(
          state = state.successData!!,
          onTransactionClick = onTransactionClick,
          onCreateTransactionClick = onCreateTransactionClick,
          commandProcessor = commandProcessor,
        )
      }
    }
  }
}

@Composable
private fun TransactionsList(
  state: TransactionsUiState,
  onTransactionClick: (Boolean, TransactionModel) -> Unit,
  onCreateTransactionClick: () -> Unit,
  commandProcessor: (TransactionListCommand) -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize()) {
    val transactions = state.transactionList.collectAsLazyPagingItems()
    val loadState = transactions.loadState
    val finishedLoading =
      loadState.refresh !is LoadState.Loading &&
        loadState.prepend !is LoadState.Loading &&
        loadState.append !is LoadState.Loading

    if (transactions.itemCount == 0 && finishedLoading) {
      EmptyState(onCreateTransactionClick = onCreateTransactionClick)
    } else {
      LazyColumn(modifier = Modifier.fillMaxSize()) {
        val count = transactions.itemCount
        items(count = count) { item ->
          when (val uiModel = transactions[item]) {
            is TransactionsListViewModel.UiModel.TransactionItem -> {
              TransactionItem(
                transaction = uiModel.transaction,
                onClick = { onTransactionClick(true, uiModel.transaction) },
                onDuplicateClick = { onTransactionClick(false, uiModel.transaction) },
                onDeleteClick = { commandProcessor(ShowDeleteTransactionConfirmationBottomSheetCommand(uiModel.transaction)) },
              )
            }

            is TransactionsListViewModel.UiModel.SeparatorItem -> {
              SeparatorItem(separator = uiModel)
            }

            else -> Unit
          }
        }
      }
    }
    when (transactions.loadState.refresh) {
      is LoadState.Loading -> ExpLoadingWheel(modifier = Modifier.align(Alignment.Center))
      is LoadState.Error -> Text("Error loading transaction list")
      else -> Unit
    }
  }
}

@Composable
fun SeparatorItem(separator: TransactionsListViewModel.UiModel.SeparatorItem) {
  val locale = androidx.compose.ui.text.intl.Locale.current
  val text = DateTimeFormatter
    .ofLocalizedDate(FormatStyle.FULL)
    .withLocale(Locale.forLanguageTag(locale.toLanguageTag()))
    .withZone(ZoneId.systemDefault())
    .format(separator.instant.toJavaInstant())

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(color = MaterialTheme.colorScheme.surface)
      .padding(vertical = Dimens.margin_small_x)
      .padding(top = Dimens.margin_large_x)
      .padding(horizontal = Dimens.margin_small_x),
    horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x)
  ) {
    Text(
      text = text,
      modifier = Modifier
        .padding(vertical = Dimens.margin_small_x, horizontal = Dimens.margin_small_x)
        .weight(1f),
      style = MaterialTheme.typography.bodySmall,
    )
    separator.sum?.let { sum ->
      Text(
        text = sum,
        modifier = Modifier
          .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerSmallRadiusShape)
          .padding(vertical = Dimens.margin_small_x, horizontal = Dimens.margin_large_x),
        style = MaterialTheme.typography.bodySmall,
      )
    }
  }
  ExpeDivider()
}

@Composable
private fun TransactionItem(
  transaction: TransactionModel,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  onDeleteClick: () -> Unit,
  onDuplicateClick: () -> Unit,
) {
  val isTransfer = transaction.target is AccountModel
  var expanded by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentSize(Alignment.TopStart)
  ) {
    Column(
      modifier = modifier
        .fillMaxWidth()
        .pointerInput(Unit) {
          detectTapGestures(
            onLongPress = { expanded = true },
            onTap = { onClick() }
          )
        }
        .padding(horizontal = Dimens.margin_large_x, vertical = Dimens.margin_small_x),
      verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_xx),
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(Dimens.margin_small_x)) {
        val target = transaction.targetSubcategory ?: transaction.target
        Icon(
          imageVector = target.icon.imageVector,
          contentDescription = "icon",
          modifier = Modifier.size(Dimens.icon_size),
          tint = target.color.color,
        )
        Text(
          text = target.name.stringValue(),
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.weight(1f),
        )
        Text(
          text = transaction.amount.formattedValue,
          style = MaterialTheme.typography.bodyLarge,
          color = transaction.textColor()
        )
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (isTransfer) {
          HorizontalSpacer(width = Dimens.icon_size)
          Icon(
            imageVector = ExpeIcons.SubdirectoryArrowRight,
            modifier = Modifier.size(Dimens.icon_size_small),
            contentDescription = null,
          )
        } else {
          HorizontalSpacer(width = Dimens.icon_size)
        }

        HorizontalSpacer(width = Dimens.margin_small_xx)
        Icon(
          imageVector = transaction.source.icon.imageVector,
          contentDescription = null,
          modifier = Modifier.size(Dimens.icon_size_small)
        )
        HorizontalSpacer(width = Dimens.margin_small_xx)
        Text(
          text = transaction.source.name.stringValue(),
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.weight(1f),
        )
        HorizontalSpacer(width = Dimens.margin_small_xx)

        if (isTransfer) {
          transaction.transferReceivedAmount?.formattedValue?.let { amount ->
            Text(
              text = amount,
              style = MaterialTheme.typography.bodyLarge,
            )
          }
        }

        ExpeDropdownMenu(
          expanded = expanded,
          onDismissRequest = { expanded = false },
          items = persistentListOf(
            DropdownMenuItem(
              text = stringResource(R.string.transactions_list_duplicate_action),
              icon = ExpeIcons.FileCopy,
              onClick = onDuplicateClick,
            ),
            DropdownMenuItem(
              text = stringResource(R.string.transactions_list_delete_action),
              textColor = MaterialTheme.colorScheme.error,
              icon = ExpeIcons.Delete,
              iconColor = MaterialTheme.colorScheme.error,
              onClick = onDeleteClick,
            ),
          )
        )
      }
      transaction.note?.let { note ->
        Text(
          text = note,
          style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
          modifier = Modifier.padding(bottom = Dimens.margin_small_x),
        )
      }
    }
  }
  ExpeDivider(
    modifier = Modifier
      .padding(start = Dimens.margin_large_x + Dimens.icon_size + Dimens.margin_small_xx)
  )
}

@Composable
private fun EmptyState(onCreateTransactionClick: () -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(Dimens.margin_large_x),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = stringResource(R.string.transactions_list_empty_state_title),
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(Dimens.margin_large_x),
    )
    Text(
      text = stringResource(R.string.transactions_list_empty_state_message),
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(horizontal = Dimens.margin_large_x),
    )
    ExpeButton(
      text = stringResource(R.string.transactions_list_create_transaction_action),
      onClick = onCreateTransactionClick,
      modifier = Modifier
        .fillMaxWidth()
        .padding(Dimens.margin_large_x),
    )
  }
}

@Composable
private fun ColumnScope.BottomSheetContent(sheetData: BottomSheetData) {
  when (sheetData) {
    is GeneralBottomSheetData -> GeneralBottomSheet(sheetData)
  }
}

@Composable
@ReadOnlyComposable
private fun TransactionModel.textColor() =
  if (type == TransactionType.INCOME) MaterialTheme.customColorsPalette.successColor else LocalContentColor.current
