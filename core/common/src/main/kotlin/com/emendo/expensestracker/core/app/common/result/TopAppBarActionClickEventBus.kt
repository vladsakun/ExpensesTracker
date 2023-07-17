package com.emendo.expensestracker.core.app.common.result

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopAppBarActionClickEventBus @Inject constructor() {

  var registeredCallback: () -> Unit = {}

  fun actionClicked() {
    registeredCallback.invoke()
  }
}