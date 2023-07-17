package com.emendo.accounts.add

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emendo.expensestracker.core.designsystem.utils.AccountState
import com.emendo.expensestracker.core.designsystem.utils.AccountStateSaver
import com.emendo.expensestracker.feature.accounts.R
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun AddAccountScreen(
  viewModel: AddAccountViewModel = hiltViewModel()
) {
  val accountNameState by rememberSaveable(stateSaver = AccountStateSaver) {
    mutableStateOf(AccountState())
  }

  viewModel.registerListener()

  Column(modifier = Modifier.padding(16.dp)) {
    OutlinedTextField(
      value = accountNameState.text,
      onValueChange = {
        viewModel.accountName = it
        accountNameState.text = it
      },
      label = {
        Text(
          text = stringResource(id = R.string.account_name),
          style = MaterialTheme.typography.bodyMedium
        )
      },
      textStyle = MaterialTheme.typography.bodyMedium,
      isError = accountNameState.showErrors(),
    )
  }
}