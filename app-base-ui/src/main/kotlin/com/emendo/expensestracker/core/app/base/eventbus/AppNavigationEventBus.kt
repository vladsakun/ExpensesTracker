package com.emendo.expensestracker.core.app.base.eventbus

import kotlinx.coroutines.flow.Flow

interface AppNavigationEventBus {
  val eventFlow: Flow<AppNavigationEvent>
  fun navigate(direction: AppNavigationEvent)
}