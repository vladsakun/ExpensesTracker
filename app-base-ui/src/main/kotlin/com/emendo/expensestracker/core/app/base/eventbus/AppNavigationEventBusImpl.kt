package com.emendo.expensestracker.core.app.base.eventbus

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class AppNavigationEventBusImpl @Inject constructor() : AppNavigationEventBus {

  private val eventChannel = Channel<AppNavigationEvent>(Channel.CONFLATED)
  override val eventFlow: Flow<AppNavigationEvent> = eventChannel.receiveAsFlow()

  override fun navigate(direction: AppNavigationEvent) {
    eventChannel.trySend(direction)
  }
}