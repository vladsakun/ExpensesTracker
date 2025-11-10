package com.emendo.expensestracker.settings.help

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
internal fun HelpArticleDetailScreen(
  article: HelpArticle,
  navigator: DestinationsNavigator,
) {
  HelpArticleDetailScreen(
    article = article,
    onNavigationClick = { navigator.navigateUp() }
  )
}

@Composable
private fun HelpArticleDetailScreen(
  article: HelpArticle,
  onNavigationClick: (() -> Unit)? = null,
) {
  ExpeScaffoldWithTopBar(
    title = stringResource(id = article.titleRes),
    onNavigationClick = onNavigationClick,
  ) { paddingValues ->
    SelectionContainer {
      Box(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(Dimens.margin_large_x)
          .padding(paddingValues)
      ) {
        val htmlText = stringResource(id = article.contentRes)
        Text(text = AnnotatedString.Companion.fromHtml(htmlText))
      }
    }
  }
}
