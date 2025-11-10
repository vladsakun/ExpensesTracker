package com.emendo.expensestracker.settings.help

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.settings.destinations.HelpArticleDetailScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
internal fun HelpArticlesListScreen(
  navigator: DestinationsNavigator,
) {
  HelpArticlesListScreen(
    onArticleClick = { article -> navigator.navigate(HelpArticleDetailScreenDestination(article)) },
    onNavigationClick = navigator::navigateUp
  )
}

@Composable
private fun HelpArticlesListScreen(onArticleClick: (HelpArticle) -> Unit, onNavigationClick: (() -> Unit)? = null) {
  ExpeScaffoldWithTopBar(
    titleResId = R.string.help,
    onNavigationClick = onNavigationClick,
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(vertical = Dimens.margin_large_x),
    ) {
      items(helpArticles) { article ->
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onArticleClick(article) }
            .padding(horizontal = Dimens.margin_large_x, vertical = Dimens.margin_normal),
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = article.icon,
              contentDescription = null,
              modifier = Modifier
                .padding(end = Dimens.margin_normal)
                .size(Dimens.icon_size)
            )
            Text(
              text = stringResource(id = article.titleRes),
              style = MaterialTheme.typography.bodyLarge
            )
          }
        }
        ExpeDivider(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = Dimens.icon_size + Dimens.margin_large_x * 2, end = Dimens.margin_large_x),
        )
      }
    }
  }
}
