package com.emendo.expensestracker.core.app.base.shared.currency

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectCurrencyScreenDestination
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.ExpeTextFieldClearIcon
import com.emendo.expensestracker.core.designsystem.component.ExpeTextFieldWithRoundedBackground
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.core.ui.bottomsheet.BottomScreenTransition
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.scope.AnimatedDestinationScope
import com.ramcosta.composedestinations.scope.resultRecipient

@Destination(style = BottomScreenTransition::class)
@Composable
fun SelectCurrencyScreen(
  navigator: DestinationsNavigator,
  resultNavigator: ResultBackNavigator<String>,
  viewModel: SelectCurrencyViewModel = hiltViewModel(),
) {
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val filteredCurrencies = viewModel.filteredCurrencies.collectAsStateWithLifecycle()
  ExpeScaffoldWithTopBar(
    titleResId = R.string.select_currency,
    onNavigationClick = navigator::navigateUp,
  ) { paddingValues ->
    val currencies = filteredCurrencies.value

    if (currencies != null) {
      val layoutDirection = LocalLayoutDirection.current
      Column(modifier = Modifier.fillMaxSize()) {
        ExpeTextFieldWithRoundedBackground(
          placeholder = "Search currency...",
          text = searchQuery,
          onValueChange = viewModel::onSearchQueryChange,
          endIcon = { ExpeTextFieldClearIcon(text = searchQuery, onValueChange = viewModel::onSearchQueryChange) },
          startIcon = { Icon(imageVector = ExpeIcons.Search, contentDescription = null) },
          modifier = Modifier
            .fillMaxWidth()
            .padding(
              top = paddingValues.calculateTopPadding() + Dimens.margin_large_x,
              start = Dimens.margin_large_x,
              end = Dimens.margin_large_x,
            ),
        )
        LazyColumn(
          modifier = Modifier.weight(1f),
          contentPadding = PaddingValues(
            start = paddingValues.calculateStartPadding(layoutDirection),
            end = paddingValues.calculateEndPadding(layoutDirection),
            top = Dimens.margin_large_x,
            bottom = paddingValues.calculateBottomPadding() + Dimens.margin_large_x,
          ),
        ) {
          items(
            items = currencies,
            key = CurrencyModel::currencyCode,
            contentType = { _ -> "currency" },
          ) { currency ->
            CurrencyItem(
              currencyModel = currency,
              onClick = { resultNavigator.navigateBack(currency.currencyCode) },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CurrencyItem(
  currencyModel: CurrencyModel,
  onClick: () -> Unit,
) {
  Column(
    modifier = Modifier.clickable(onClick = onClick)
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

@Composable
fun AnimatedDestinationScope<*>.selectCurrencyResultRecipient() =
  resultRecipient<SelectCurrencyScreenDestination, String>()