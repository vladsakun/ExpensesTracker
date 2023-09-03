package com.emendo.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.data.model.ColorModel
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffold
import com.emendo.expensestracker.core.designsystem.component.ExpeTextField
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(start = true)
@Composable
fun TransactionsScreen(
  navigator: DestinationsNavigator,
  viewModel: TransactionsScreenViewModel = hiltViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val onColorClick = remember(viewModel) { { viewModel.onColorChange() } }
  ExpeScaffold { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      ExpeTextField(label = "Account name", text = state.accountName, onValueChange = viewModel::nameChanged)
      Icon(imageVector = state.icon.imageVector, contentDescription = "Icon")
      ColorBox(onColorClick, state.color)
    }
  }
}

@Composable
private fun ColorBox(
  onColorClick: () -> Unit,
  colorModel: ColorModel,
) {
  Box(
    modifier = Modifier
      .size(40.dp)
      .background(colorModel.color)
      .clickable(onClick = onColorClick)
  )
}

@Composable
private fun TransactionsScreenContent(
  onNameChanged: (String) -> Unit,
  state: State<TransactionsScreenData>,
) {
  ExpeScaffold { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      ExpeTextField(label = "Account name", text = state.value.accountName, onValueChange = onNameChanged)
      Icon(imageVector = state.value.icon.imageVector, contentDescription = "Icon")
      Box(
        modifier = Modifier
          .size(40.dp)
          .background(state.value.color.color),
      )
    }
  }
}