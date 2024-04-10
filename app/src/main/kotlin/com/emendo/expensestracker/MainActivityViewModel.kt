package com.emendo.expensestracker

import androidx.lifecycle.ViewModel
import com.emendo.expensestracker.data.api.manager.ExpeLocaleManager
import com.emendo.expensestracker.data.api.manager.ExpeTimeZoneManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  private val localeManager: ExpeLocaleManager,
  private val timeZoneManager: ExpeTimeZoneManager,
) : ViewModel() {

  fun updateLocale() {
    localeManager.onLocaleChange()
  }

  fun updateTimeZone() {
    timeZoneManager.onZoneChange()
  }
}