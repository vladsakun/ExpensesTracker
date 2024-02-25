package com.emendo.expensestracker.core.app.base.app.base

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter

abstract class BaseBroadcastReceiver : BroadcastReceiver() {
  private var registered: Boolean = false

  abstract val filter: IntentFilter

  @SuppressLint("UnspecifiedRegisterReceiverFlag")
  fun register(context: Context) {
    if (registered) {
      return
    }

    context.registerReceiver(this, filter)
    registered = true
  }

  fun unregister(context: Context) {
    if (!registered) {
      return
    }

    context.unregisterReceiver(this)
    registered = false
  }
}