package com.emendo.expensestracker.app.base.api

import kotlinx.coroutines.flow.Flow

interface AppNavigationEventBus {
  val eventFlow: Flow<AppNavigationEvent>
  fun navigate(direction: AppNavigationEvent)
}