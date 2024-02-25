package com.emendo.expensestracker.core.app.base.eventbus

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class AppNavigationEventBusImpl @Inject constructor() : com.emendo.expensestracker.app.base.api.AppNavigationEventBus {

  private val eventChannel = Channel<com.emendo.expensestracker.app.base.api.AppNavigationEvent>(Channel.CONFLATED)
  override val eventFlow: Flow<com.emendo.expensestracker.app.base.api.AppNavigationEvent> =
    eventChannel.receiveAsFlow()

  override fun navigate(direction: com.emendo.expensestracker.app.base.api.AppNavigationEvent) {
    eventChannel.trySend(direction)
  }
}