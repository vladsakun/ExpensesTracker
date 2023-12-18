package com.emendo.expensestracker.core.app.base.app.base

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class TimeZoneBroadcastReceiver(
  private val onTimeZoneChanged: () -> Unit,
) : BaseBroadcastReceiver() {

  override val filter: IntentFilter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == Intent.ACTION_TIMEZONE_CHANGED) {
      onTimeZoneChanged()
    }
  }
}