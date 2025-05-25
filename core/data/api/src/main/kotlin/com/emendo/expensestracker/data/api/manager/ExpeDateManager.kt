package com.emendo.expensestracker.data.api.manager

import kotlinx.coroutines.flow.Flow

interface ExpeDateManager {
  val dateChangedEvent: Flow<Unit>
  fun onDateChange()
}