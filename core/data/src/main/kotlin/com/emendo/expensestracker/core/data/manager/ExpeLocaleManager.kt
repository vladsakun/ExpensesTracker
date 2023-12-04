package com.emendo.expensestracker.core.data.manager

import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

interface ExpeLocaleManager {
  val localeState: StateFlow<Locale>
  fun getLocale(): Locale
  fun onLocaleChange()
}