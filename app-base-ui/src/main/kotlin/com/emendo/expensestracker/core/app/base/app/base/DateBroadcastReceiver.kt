package com.emendo.expensestracker.core.app.base.app.base

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class DateBroadcastReceiver(
  private val onDateChanged: () -> Unit,
) : BaseBroadcastReceiver() {

  override val filter: IntentFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == Intent.ACTION_DATE_CHANGED) {
      onDateChanged()
    }
  }
}