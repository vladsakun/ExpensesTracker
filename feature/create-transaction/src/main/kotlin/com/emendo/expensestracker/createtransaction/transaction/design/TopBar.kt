package com.emendo.expensestracker.createtransaction.transaction.design

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeCenterAlignedTopBar
import com.emendo.expensestracker.core.designsystem.component.MenuAction
import com.emendo.expensestracker.core.designsystem.component.NavigationBackIcon
import com.emendo.expensestracker.core.designsystem.component.TextSwitch
import com.emendo.expensestracker.core.model.data.TransactionType.Companion.toTransactionType
import com.emendo.expensestracker.createtransaction.transaction.CreateTransactionUiState
import com.emendo.expensestracker.createtransaction.transaction.data.ChangeTransactionTypeCommand
import com.emendo.expensestracker.createtransaction.transaction.data.CreateTransactionCommand
import com.emendo.expensestracker.createtransaction.transaction.data.SaveTransactionCommand
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBar(
  stateProvider: () -> CreateTransactionUiState,
  commandProcessor: (CreateTransactionCommand) -> Unit,
  onBackPressed: () -> Unit,
) {
  ExpeCenterAlignedTopBar(
    title = {
      val tabsResId = persistentListOf(R.string.income, R.string.expense, R.string.transfer)
      // Todo improve transaction type switch animation
      TextSwitch(
        selectedIndex = stateProvider().screenData.transactionType.ordinal,
        items = tabsResId.map { stringResource(id = it) }.toPersistentList(),
        onSelectionChange = { tabIndex ->
          commandProcessor(ChangeTransactionTypeCommand(toTransactionType(tabIndex)))
        },
      )
    },
    navigationIcon = { NavigationBackIcon(onNavigationClick = onBackPressed) },
    actions = persistentListOf(
      MenuAction(
        icon = ExpeIcons.Check,
        onClick = { commandProcessor(SaveTransactionCommand()) },
        contentDescription = stringResource(id = R.string.confirm),
      )
    ),
  )
}