package com.emendo.expensestracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.emendo.expensestracker.broadcast.LocaleBroadcastReceiver
import com.emendo.expensestracker.core.app.base.app.base.TimeZoneBroadcastReceiver
import com.emendo.expensestracker.core.app.common.ext.collectWhenStarted
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.ui.ExpeApp
import com.ramcosta.composedestinations.navigation.navigate
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  val viewModel: MainActivityViewModel by viewModels()

  private val localeBroadcastReceiver by lazy { LocaleBroadcastReceiver(viewModel::updateLocale) }
  private val timeZoneBroadcastReceiver by lazy { TimeZoneBroadcastReceiver(viewModel::updateTimeZone) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Turn off the decor fitting system windows, which allows us to handle insets,
    // including IME animations, and go edge-to-edge
    // This also sets up the initial system bar style based on the platform theme
    enableEdgeToEdge()

    setContent {
      val darkTheme = isSystemInDarkTheme()
      val navController = rememberNavController()

      LaunchedEffect(viewModel.navigationEvent) {
        viewModel.navigationEvent.collectWhenStarted(this@MainActivity) { event ->
          if (event.navigateUp) {
            navController.navigateUp()
          }
          navController.navigate(event.direction)
        }
      }

      // Update the dark content of the system bars to match the theme
      DisposableEffect(darkTheme) {
        enableEdgeToEdge(
          statusBarStyle = SystemBarStyle.auto(
            android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT,
          ) { darkTheme },
          navigationBarStyle = SystemBarStyle.auto(
            lightScrim,
            darkScrim,
          ) { darkTheme },
        )
        onDispose {}
      }

      ExpensesTrackerTheme(darkTheme = darkTheme) {
        ExpeApp(
          windowSizeClass = calculateWindowSizeClass(this),
          navController = navController,
        )
      }
    }
  }

  override fun onStart() {
    super.onStart()
    localeBroadcastReceiver.register(this)
    timeZoneBroadcastReceiver.register(this)
  }

  override fun onStop() {
    super.onStop()
    localeBroadcastReceiver.unregister(this)
    timeZoneBroadcastReceiver.unregister(this)
  }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)