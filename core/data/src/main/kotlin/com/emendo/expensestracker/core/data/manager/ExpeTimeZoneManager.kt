package com.emendo.expensestracker.core.data.manager

import kotlinx.coroutines.flow.StateFlow
import java.time.ZoneId

interface ExpeTimeZoneManager {
  val timeZoneState: StateFlow<ZoneId>
  fun getZoneId(): ZoneId
  fun onZoneChange()
}