package com.emendo.transactions

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import com.emendo.expensestracker.core.designsystem.component.ExpeTopAppBar
import com.emendo.expensestracker.feature.transactions.R
import com.emendo.transactions.destinations.TransactionsScreen2Destination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(start = true)
@Composable
fun TransactionsScreen(
  navigator: DestinationsNavigator
) {
  Scaffold() { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Button(
        onClick = {
          navigator.navigate(TransactionsScreen2Destination)
        }) {
        Text(text = "Transactions2")
      }
    }
  }
}