package com.emendo.expensestracker

import android.app.Application
import com.emendo.expensestracker.core.android.api.OnAppCreate
import com.emendo.expensestracker.sync.initializers.Sync
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ExpensesTrackerApplication : Application() {

  @Inject
  lateinit var appCreatePlugins: @JvmSuppressWildcards Set<OnAppCreate>

  companion object {
    var applicationScope = MainScope()
  }

  override fun onCreate() {
    super.onCreate()
    // Initialize Sync; the system responsible for keeping data in the app up to date.
    Sync.initialize(context = this)
    Timber.plant(Timber.DebugTree())

    initApp()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    applicationScope.cancel("onLowMemory() called by system")
    applicationScope = MainScope()
  }

  private fun initApp() {
    applicationScope.launch {
      appCreatePlugins.forEach { it.onCreate() }
    }
  }
}