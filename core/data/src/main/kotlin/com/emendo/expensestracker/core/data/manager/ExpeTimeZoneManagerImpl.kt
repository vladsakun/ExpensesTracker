package com.emendo.expensestracker.core.data.manager

import com.emendo.expensestracker.data.api.manager.ExpeTimeZoneManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.ZoneId
import javax.inject.Inject

class ExpeTimeZoneManagerImpl @Inject constructor() : ExpeTimeZoneManager {
  private val _timeZoneState: MutableStateFlow<ZoneId> = MutableStateFlow(ZoneId.systemDefault())
  override val timeZoneState: StateFlow<ZoneId> = _timeZoneState.asStateFlow()

  override fun getZoneId(): ZoneId =
    ZoneId.systemDefault()

  override fun onZoneChange() {
    _timeZoneState.update { ZoneId.systemDefault() }
  }
}