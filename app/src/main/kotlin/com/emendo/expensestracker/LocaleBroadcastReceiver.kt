package com.emendo.expensestracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocaleBroadcastReceiver(private val onLocaleChange: () -> Unit) : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    onLocaleChange()
  }
}