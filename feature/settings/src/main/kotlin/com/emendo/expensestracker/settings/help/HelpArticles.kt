package com.emendo.expensestracker.settings.help

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons

enum class HelpArticle(
  @StringRes val titleRes: Int,
  @StringRes val contentRes: Int,
  val icon: ImageVector,
) {
  HOW_TO_USE(R.string.help_article_how_to_use_title, R.string.help_screen_how_to_use, ExpeIcons.Help),
  CURRENCY_SETTINGS(
    R.string.help_article_currency_settings_title,
    R.string.help_screen_currency_settings,
    ExpeIcons.Currency
  ),
  APP_LANGUAGE(R.string.help_article_app_language_title, R.string.help_screen_app_language, ExpeIcons.Language),
  QUESTIONS(R.string.help_article_questions_title, R.string.help_screen_questions, ExpeIcons.QuestionMark),
}

val helpArticles = HelpArticle.entries
