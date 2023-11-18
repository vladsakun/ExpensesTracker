package com.emendo.expensestracker.core.data.manager

import java.util.Locale
import javax.inject.Inject

class ExpeLocaleManagerImpl @Inject constructor() : ExpeLocaleManager {
  override fun getLocale(): Locale = Locale.getDefault()
}