package com.emendo.expensestracker

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ExpensesTrackerApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "onCreate: ")
  }

  companion object {
    private const val TAG = "ExpensesTrackerApplicat"
  }
}