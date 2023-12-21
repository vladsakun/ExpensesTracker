package com.emendo.expensestracker.accounts.common.design

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.emendo.expensestracker.accounts.common.model.AccountScreenData
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.ExpeTextFieldWithRoundedBackground
import com.emendo.expensestracker.core.designsystem.component.MenuAction
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.SelectRow
import com.emendo.expensestracker.core.ui.SelectRowWithColor
import com.emendo.expensestracker.core.ui.SelectRowWithIcon
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun AccountContent(
  stateProvider: () -> AccountScreenData,
  title: String,
  onNavigationClick: () -> Unit,
  onNameChange: (String) -> Unit,
  onIconRowClick: () -> Unit,
  onColorRowClick: () -> Unit,
  onBalanceRowClick: () -> Unit,
  onCurrencyRowClick: () -> Unit,
  onConfirmClick: () -> Unit,
  endContent: @Composable ColumnScope.() -> Unit,
) {
  ExpeScaffoldWithTopBar(
    title = title,
    onNavigationClick = onNavigationClick,
    actions = persistentListOf(
      MenuAction(
        icon = ExpeIcons.Check,
        onClick = onConfirmClick,
        contentDescription = stringResource(id = R.string.confirm),
      )
    ),
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .verticalScroll(rememberScrollState())
        .padding(paddingValues)
        .padding(Dimens.margin_large_x),
      verticalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
    ) {
      ExpeTextFieldWithRoundedBackground(
        placeholder = stringResource(id = R.string.account_name),
        text = stateProvider().name,
        onValueChange = onNameChange,
      )
      SelectRowWithIcon(
        labelResId = R.string.icon,
        imageVectorProvider = { stateProvider().icon.imageVector },
        onClick = onIconRowClick,
      )
      SelectRowWithColor(
        labelResId = R.string.color,
        colorProvider = { stateProvider().color },
        onClick = onColorRowClick,
      )
      SelectRow(
        labelResId = R.string.balance,
        onClick = onBalanceRowClick,
        labelModifier = { Modifier.weight(1f) },
        endLayout = {
          Text(
            text = stateProvider().balance.formattedValue,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
          )
        }
      )
      SelectRow(
        labelResId = R.string.currency,
        onClick = onCurrencyRowClick,
        labelModifier = { Modifier.weight(1f) }
      ) {
        Text(
          text = stateProvider().currency.currencySymbolOrCode,
          style = MaterialTheme.typography.bodyLarge,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          textAlign = TextAlign.End,
        )
      }
      endContent()
    }
  }
}