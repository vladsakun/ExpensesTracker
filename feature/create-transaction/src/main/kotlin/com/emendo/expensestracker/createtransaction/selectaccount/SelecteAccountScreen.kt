package com.emendo.expensestracker.createtransaction.selectaccount

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.resources.models.ColorModel.Companion.color
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.emendo.expensestracker.core.ui.AccountItem
import com.emendo.expensestracker.core.ui.stringValue
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.emendo.expensestracker.core.app.resources.R as AppR

@Destination
@Composable
fun SelectAccountScreen(
  navigator: DestinationsNavigator,
  viewModel: SelectAccountViewModel = hiltViewModel(),
) {
  ExpeScaffoldWithTopBar(
    titleResId = AppR.string.accounts,
    onNavigationClick = navigator::navigateUp
  ) { paddingValues ->
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      when (val state = uiState.value) {
        is SelectAccountUiState.Empty -> uniqueItem("empty") {
          Text(text = "You have zero accounts. Create one 😊")
        }

        is SelectAccountUiState.Error -> uniqueItem("error") {
          Text(text = state.message)
        }

        is SelectAccountUiState.Loading -> uniqueItem("loader") { ExpLoadingWheel() }

        is SelectAccountUiState.DisplayAccountsList -> {
          items(
            items = state.accountModels,
            key = { account -> account.id },
            contentType = { "account" },
          ) { account ->
            AccountItem(
              color = account.color.color,
              icon = account.icon.imageVector,
              name = account.name.stringValue(),
              balance = account.balanceFormatted,
              onClick = {
                viewModel.selectAccount(account)
                navigator.navigateUp()
              }
            )
            ExpeDivider()
          }
        }

        is SelectAccountUiState.Default -> Unit
      }
    }
  }
}

