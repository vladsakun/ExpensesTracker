package com.emendo.accounts.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeTopAppBar
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.AccountNameStateSaver
import com.emendo.expensestracker.core.designsystem.utils.AccountState
import com.emendo.expensestracker.core.designsystem.utils.TextFieldState
import com.emendo.expensestracker.feature.accounts.R
import com.ramcosta.composedestinations.annotation.Destination
import com.emendo.expensestracker.core.app.resources.R as AppR

@Composable
@Destination
fun CreateAccountScreen(
  viewModel: CreateAccountViewModel = hiltViewModel()
) {
  viewModel.registerListener()

  val accountNameState by rememberSaveable(stateSaver = AccountNameStateSaver) {
    mutableStateOf(AccountState())
  }

  CreateAccountScreenContent(accountNameState, {
    viewModel.setAccountName(it)
    accountNameState.text = it
  })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateAccountScreenContent(
  accountNameState: TextFieldState,
  onAccountNameChange: (accountName: String) -> Unit = {},
  onCloseClick: () -> Unit = {},
  onCreateAccountClick: () -> Unit = {},
) {
  Scaffold(topBar = {
    LargeTopAppBar(
      title = {
        TextField(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.margin_large_x),
          value = accountNameState.text,
          onValueChange = onAccountNameChange,
          textStyle = MaterialTheme.typography.headlineLarge,
          placeholder = {
            Text(text = stringResource(id = R.string.account_name))
          }
        )
      },
      navigationIcon = {
        IconButton(onClick = onCloseClick) {
          Icon(
            imageVector = ExpIcons.Close,
            contentDescription = stringResource(id = AppR.string.close),
            tint = MaterialTheme.colorScheme.onPrimary
          )
        }
      },
      actions = {
        IconButton(onClick = onCreateAccountClick) {
          Icon(
            imageVector = ExpIcons.Check,
            contentDescription = stringResource(id = AppR.string.close),
            tint = MaterialTheme.colorScheme.onPrimary
          )
        }
      }
    )
  }) { paddingValues ->
    Box(
      modifier = Modifier
        .padding(paddingValues)
        .size(200.dp)
        .background(Color.Black)
    )
  }
  //  Column() {
  //    Column(
  //      modifier = Modifier
  //        .fillMaxWidth()
  //        .background(color = MaterialTheme.colorScheme.primary)
  //        .padding(bottom = Dimens.margin_large_x),
  //    ) {
  //
  //      ExpeTopAppBar(
  //        titleRes = R.string.accounts,
  //        actionIcon = ExpIcons.Check,
  //        navigationIcon = ExpIcons.Close,
  //        actionIconContentDescription = "test",
  //        navigationIconContentDescription = "test",
  //        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
  //          containerColor = MaterialTheme.colorScheme.primary,
  //        )
  //      )
  //      TextField(
  //        modifier = Modifier
  //          .fillMaxWidth()
  //          .padding(horizontal = Dimens.margin_large_x),
  //        value = accountNameState.text,
  //        onValueChange = onAccountNameChange,
  //        textStyle = MaterialTheme.typography.headlineLarge,
  //      )
  //      //      OutlinedTextField(
  //      //        modifier = Modifier
  //      //          .fillMaxWidth()
  //      //          .padding(horizontal = Dimens.margin_large_x),
  //      //        value = accountNameState.text,
  //      //        onValueChange = {
  //      //          onAccountNameChange(it)
  //      //        },
  //      //        placeholder = {
  //      //          Text(
  //      //            text = stringResource(id = R.string.account_name),
  //      //            style = MaterialTheme.typography.labelSmall
  //      //          )
  //      //        },
  //      //        label = {
  //      //          Text(
  //      //            text = stringResource(id = R.string.account_name),
  //      //            style = MaterialTheme.typography.labelSmall
  //      //          )
  //      //        },
  //      //        isError = accountNameState.showErrors(),
  //      //      )
  //    }
  //    Column(
  //      modifier = Modifier
  //        .padding(Dimens.margin_large_x),
  //      verticalArrangement = Arrangement.spacedBy(10.dp)
  //    ) {
  //      Surface(
  //        tonalElevation = 3.dp,
  //        modifier = Modifier
  //          .fillMaxWidth()
  //          .padding(Dimens.margin_large_x)
  //          .verticalScroll(rememberScrollState()),
  //      ) {
  //        Column(
  //          verticalArrangement = Arrangement.spacedBy(10.dp),
  //          horizontalAlignment = Alignment.CenterHorizontally
  //        ) {
  //          Box(
  //            modifier = Modifier
  //              .size(200.dp)
  //              .background(Color.Black)
  //          )
  //          Box(
  //            modifier = Modifier
  //              .size(200.dp)
  //              .background(Color.Black)
  //          )
  //          Box(
  //            modifier = Modifier
  //              .size(200.dp)
  //              .background(Color.Black)
  //          )
  //          Box(
  //            modifier = Modifier
  //              .size(200.dp)
  //              .background(Color.Black)
  //          )
  //        }
  //      }
  //    }
  //  }
}

@Preview
@Composable
fun CreateAccountScreenPreview() {
  CreateAccountScreenContent(accountNameState = TextFieldState())
}