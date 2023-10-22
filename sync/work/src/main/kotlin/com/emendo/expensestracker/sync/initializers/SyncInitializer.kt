package com.emendo.expensestracker.sync.initializers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.emendo.expensestracker.sync.SyncCurrencyRatesWorker

object Sync {
  // This method is initializes sync, the process that keeps the app's data current.
  // It is called from the app module's Application.onCreate() and should be only done once.
  fun initialize(context: Context) {
    WorkManager.getInstance(context).apply {
      enqueueUniquePeriodicWork(
        SyncWorkName,
        ExistingPeriodicWorkPolicy.KEEP,
        SyncCurrencyRatesWorker.startUpSyncWork(),
      )
    }
  }
}

// This name should not be changed otherwise the app may have concurrent sync requests running
internal const val SyncWorkName = "CurrencyRatesSyncWorkName"