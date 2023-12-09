package com.emendo.expensestracker.sync.initializers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.emendo.expensestracker.core.model.data.CategoryWithOrdinalIndex
import com.emendo.expensestracker.sync.workers.ReorderCategoriesWorker
import com.emendo.expensestracker.sync.workers.SyncCurrencyRatesWorker

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

  fun initializeReorderCategories(
    context: Context,
    eventsToHandle: List<Long>,
    categories: List<CategoryWithOrdinalIndex>,
  ) {
    WorkManager.getInstance(context).apply {
      enqueueUniqueWork(
        ReorderCategoriesWorkName,
        ExistingWorkPolicy.REPLACE,
        ReorderCategoriesWorker.startUpReorderCategoriesWorker(eventsToHandle, categories)
      )
    }
  }
}

// This name should not be changed otherwise the app may have concurrent sync requests running
internal const val SyncWorkName = "CurrencyRatesSyncWorkName"
internal const val ReorderCategoriesWorkName = "ReorderCategoriesWorkName"