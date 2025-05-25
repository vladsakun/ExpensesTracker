package com.emendo.expensestracker.core.data.manager

import com.emendo.expensestracker.data.api.manager.ExpeDateManager
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class ExpeDateManagerImpl @Inject constructor() : ExpeDateManager {

  private val _dateChangedEvent: MutableSharedFlow<Unit> =
    MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
  override val dateChangedEvent: Flow<Unit> = _dateChangedEvent

  override fun onDateChange() {
    _dateChangedEvent.tryEmit(Unit)
  }
}