package com.emendo.expensestracker.core.app.base.eventbus

import com.emendo.expensestracker.core.data.model.transaction.TransactionSource
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

sealed interface AppNavigationEvent {
  data class CreateTransaction(
    val source: TransactionSource?,
    val target: TransactionTarget?,
  ) : AppNavigationEvent
}

class AppNavigationEventBusImpl @Inject constructor() : AppNavigationEventBus {

  private val eventChannel = Channel<AppNavigationEvent>(Channel.CONFLATED)
  override val eventFlow: Flow<AppNavigationEvent> = eventChannel.receiveAsFlow()

  override fun navigate(direction: AppNavigationEvent) {
    eventChannel.trySend(direction)
  }
}