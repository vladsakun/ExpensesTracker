package com.emendo.expensestracker.data.api.manager

import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

interface ExpeLocaleManager {
  val localeState: StateFlow<Locale>
  fun getLocale(): Locale
  fun onLocaleChange()
}