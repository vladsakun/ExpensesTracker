package com.emendo.expensestracker

import android.app.Application
import com.emendo.expensestracker.core.data.manager.AppInitManager
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

  @Inject
  lateinit var appInitializer: AppInitManager

  override fun onCreate() {
    super.onCreate()
    applicationScope.launch {
      appInitializer.init()
    }
    Timber.plant(Timber.DebugTree())
  }

  override fun onLowMemory() {
    super.onLowMemory()
    applicationScope.cancel("onLowMemory() called by system")
    applicationScope = MainScope()
  }
}