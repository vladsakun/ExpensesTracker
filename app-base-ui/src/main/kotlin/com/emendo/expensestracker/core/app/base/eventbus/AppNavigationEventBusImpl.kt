package com.emendo.expensestracker.core.app.base.eventbus

import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

sealed interface AppNavigationEvent {
  data class CreateTransaction(
    val source: TransactionSource?,
    val target: TransactionTarget?,
    val payload: CreateTransactionEventPayload? = null,
    val shouldNavigateUp: Boolean = false,
  ) : AppNavigationEvent

  data class CreateCategory(val categoryType: CategoryType) : AppNavigationEvent
  data class SelectAccount(val isTransferTargetSelect: Boolean = false) : AppNavigationEvent
  data object SelectCurrency : AppNavigationEvent
  data class SelectColor(val preselectedColorId: Int) : AppNavigationEvent
  data class SelectIcon(val preselectedIconId: Int) : AppNavigationEvent
}

class AppNavigationEventBusImpl @Inject constructor() : AppNavigationEventBus {

  private val eventChannel = Channel<AppNavigationEvent>(Channel.CONFLATED)
  override val eventFlow: Flow<AppNavigationEvent> = eventChannel.receiveAsFlow()

  override fun navigate(direction: AppNavigationEvent) {
    eventChannel.trySend(direction)
  }
}