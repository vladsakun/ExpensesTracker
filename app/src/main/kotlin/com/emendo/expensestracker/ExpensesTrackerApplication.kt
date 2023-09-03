package com.emendo.expensestracker

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ExpensesTrackerApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "onCreate: ")
    Timber.plant(Timber.DebugTree())
  }

  companion object {
    private const val TAG = "ExpensesTrackerApplicat"
  }
}