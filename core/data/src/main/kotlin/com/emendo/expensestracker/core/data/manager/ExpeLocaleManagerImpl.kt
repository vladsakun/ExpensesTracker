package com.emendo.expensestracker.core.data.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject

class ExpeLocaleManagerImpl @Inject constructor() : ExpeLocaleManager {
  private val _localeState = MutableStateFlow(getLocale())
  override val localeState: StateFlow<Locale> = _localeState.asStateFlow()

  override fun getLocale(): Locale = Locale.getDefault()

  override fun onLocaleChange() {
    _localeState.update { getLocale() }
  }
}