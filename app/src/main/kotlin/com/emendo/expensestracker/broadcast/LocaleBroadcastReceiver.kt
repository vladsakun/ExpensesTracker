package com.emendo.expensestracker.broadcast

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.emendo.expensestracker.core.app.base.app.base.BaseBroadcastReceiver

class LocaleBroadcastReceiver(
  private val onLocaleChange: () -> Unit,
) : BaseBroadcastReceiver() {

  override val filter: IntentFilter = IntentFilter(Intent.ACTION_LOCALE_CHANGED)

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == Intent.ACTION_LOCALE_CHANGED) {
      onLocaleChange()
    }
  }
}

