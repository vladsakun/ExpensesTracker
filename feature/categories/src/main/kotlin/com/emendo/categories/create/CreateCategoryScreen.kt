package com.emendo.categories.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emendo.expensestracker.core.designsystem.utils.AccountState
import com.emendo.expensestracker.core.designsystem.utils.AccountNameStateSaver
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun CreateCategoryScreen(
  navigator: DestinationsNavigator,
  viewModel: CreateCategoryViewModel = hiltViewModel()
) {
  viewModel.registerListener()

  val categoryNameState by rememberSaveable(stateSaver = AccountNameStateSaver) {
    mutableStateOf(AccountState())
  }

  Column(
    modifier = Modifier.padding(16.dp),
  ) {
    OutlinedTextField(
      value = categoryNameState.text,
      onValueChange = {
        viewModel.categoryName = it
        categoryNameState.text = it
      },
      placeholder = {
        Text(
          text = "Category name",
          style = MaterialTheme.typography.labelSmall
        )
      },
      textStyle = MaterialTheme.typography.bodyMedium,
      isError = categoryNameState.showErrors(),
    )
  }
}