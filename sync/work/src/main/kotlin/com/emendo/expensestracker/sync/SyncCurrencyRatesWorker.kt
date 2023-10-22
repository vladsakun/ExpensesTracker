package com.emendo.expensestracker.sync

import android.content.Context
import androidx.tracing.traceAsync
import androidx.work.*
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.data.Synchronizer
import com.emendo.expensestracker.core.data.repository.api.CurrencyRatesRepository
import com.emendo.expensestracker.core.datastore.ChangeListVersions
import com.emendo.expensestracker.core.datastore.ExpePreferencesDataStore
import com.emendo.expensestracker.sync.initializers.SyncConstraints
import com.emendo.expensestracker.sync.initializers.syncForegroundInfo
import com.emendo.expensestracker.sync.workers.DelegatingWorker
import com.emendo.expensestracker.sync.workers.delegatedData
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SyncCurrencyRatesWorker @AssistedInject constructor(
  @Assisted private val appContext: Context,
  @Assisted workerParams: WorkerParameters,
  private val currencyRatesRepository: CurrencyRatesRepository,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val expePreferencesDataStore: ExpePreferencesDataStore,
) : CoroutineWorker(appContext, workerParams), Synchronizer {

  override suspend fun getForegroundInfo(): ForegroundInfo =
    appContext.syncForegroundInfo()

  override suspend fun doWork(): Result = withContext(ioDispatcher) {
    traceAsync("Sync", 0) {
      val syncedSuccessfully = awaitAll(
        async { currencyRatesRepository.sync() },
      ).all { it }

      if (syncedSuccessfully) {
        Result.success()
      } else {
        Result.retry()
      }
    }
  }

  override suspend fun getChangeListVersions(): ChangeListVersions =
    expePreferencesDataStore.getChangeListVersions()

  override suspend fun updateChangeListVersions(update: ChangeListVersions.() -> ChangeListVersions) =
    expePreferencesDataStore.updateChangeListVersion(update)

  companion object {
    fun startUpSyncWork(): PeriodicWorkRequest {
      val currentDate = Calendar.getInstance()
      val dueDate = Calendar.getInstance()

      // Set Execution around 07:00:00 AM
      dueDate.set(Calendar.HOUR_OF_DAY, 7)
      dueDate.set(Calendar.MINUTE, 0)
      dueDate.set(Calendar.SECOND, 0)
      if (dueDate.before(currentDate)) {
        dueDate.add(Calendar.HOUR_OF_DAY, 24)
      }

      val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
      val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff)

      Timber.tag("MyWorker").d("time difference $minutes")

      return PeriodicWorkRequestBuilder<DelegatingWorker>(24, TimeUnit.HOURS)
        .setInitialDelay(minutes, TimeUnit.MINUTES)
        // Todo check expedited
        .setConstraints(SyncConstraints)
        .setInputData(SyncCurrencyRatesWorker::class.delegatedData())
        .build()
    }
  }
}