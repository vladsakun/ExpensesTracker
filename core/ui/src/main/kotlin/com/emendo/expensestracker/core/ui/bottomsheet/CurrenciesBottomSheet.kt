package com.emendo.expensestracker.core.ui.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.ExpeBottomSheet
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CurrenciesBottomSheet(
  onSelectCurrency: (currencyModel: CurrencyModel) -> Unit,
  onCloseClick: () -> Unit,
  currencies: ImmutableList<CurrencyModel>,
) {
  ExpeBottomSheet(
    titleResId = R.string.currency,
    onCloseClick = onCloseClick,
    content = {
      LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
          items = currencies,
          key = { item: CurrencyModel -> item.currencyCode },
          contentType = { _ -> "currency" },
        ) { CurrencyItem(currencyModel = it, onSelectCurrency) }
        uniqueItem(key = "bottom_spacer") {
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
        text = currencyModel.currencyCode,
        style = MaterialTheme.typography.bodyLarge.copy(
          color = MaterialTheme.colorScheme.secondary,
          fontWeight = FontWeight.Bold,
        )
      )
      Text(
        text = currencyModel.currencyName,
        style = MaterialTheme.typography.bodyLarge,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
      )
      currencyModel.currencySymbol?.let {
        Text(
          text = it,
          style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold,
          ),
          modifier = Modifier.wrapContentSize(),
        )
      }
    }
    ExpeDivider()
  }
}