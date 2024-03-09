package com.emendo.expensestracker

import android.app.Application
import android.os.StrictMode
import com.emendo.expensestracker.core.android.api.OnAppCreate
import com.emendo.expensestracker.sync.initializers.Sync
import com.ramcosta.composedestinations.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ExpensesTrackerApplication : Application() {

  companion object {
    var applicationScope = MainScope()
  }

  override fun onCreate() {
    enableStrictMode()

    super.onCreate()
    // Initialize Sync; the system responsible for keeping data in the app up to date.
    Sync.initialize(context = this)
    Timber.plant(Timber.DebugTree())

    //    RebuggerConfig.init(
    //      tag = "MyAppRebugger", // changing default tag
    //      logger = { tag, message -> Timber.i(tag, message) } // use Timber for logging
    //    )
  }

  override fun onLowMemory() {
    super.onLowMemory()
    applicationScope.cancel("onLowMemory() called by system")
    applicationScope = MainScope()
  }

  @Inject
  fun initApp(
    appCreatePlugins: @JvmSuppressWildcards Set<OnAppCreate>,
  ) {
    applicationScope.launch {
      appCreatePlugins.forEach { it.onCreate() }
    }
  }

  private fun enableStrictMode() {
    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
          .detectDiskReads()
          .detectDiskWrites()
          .detectNetwork()
          // or .detectAll() for all detectable problems
          .penaltyFlashScreen()
          .penaltyLog()
          .build()
      )
      StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
          .detectLeakedSqlLiteObjects()
          .detectLeakedClosableObjects()
          .penaltyLog()
          .penaltyDeath()
          .build()
      )
    }
  }
}