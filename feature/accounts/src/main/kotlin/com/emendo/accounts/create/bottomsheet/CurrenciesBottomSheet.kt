package com.emendo.accounts.create.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.component.bottomsheet.ExpeBottomSheet
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CurrenciesBottomSheet(
  currencies: ImmutableList<CurrencyModel>,
  onSelectCurrency: (currencyModel: CurrencyModel) -> Unit,
  onCloseClick: () -> Unit,
) {
  ExpeBottomSheet(
    titleResId = R.string.currency,
    onCloseClick = onCloseClick,
    content = {
      LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
          items = currencies,
          key = { item: CurrencyModel -> item.id },
          contentType = { _ -> "currencies" },
        ) { CurrencyItem(currencyModel = it, onSelectCurrency) }
        item(key = "bottom_spacer", contentType = "spacer") {
          Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
        }
      }
    },
  )
}

@Composable
private fun CurrencyItem(
  currencyModel: CurrencyModel,
  onCurrencySelect: (currencyModel: CurrencyModel) -> Unit,
) {
  Column(
    modifier = Modifier.clickable(onClick = { onCurrencySelect(currencyModel) })
  ) {
    Row(
      modifier = Modifier.padding(Dimens.margin_large_x),
      horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x)
    ) {
      Text(
        text = currencyModel.currencyName, style = MaterialTheme.typography.bodyLarge.copy(
          color = MaterialTheme.colorScheme.secondary,
          fontWeight = FontWeight.Bold
        )
      )
      Text(
        text = currencyModel.currencySymbol,
        style = MaterialTheme.typography.bodyLarge
      )
    }
    ExpeDivider()
  }
}