package com.emendo.expensestracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.emendo.expensestracker.core.app.common.result.TopAppBarActionClickEventBus
import com.emendo.expensestracker.ui.ExpeApp
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var topAppBarActionClickEventBus: TopAppBarActionClickEventBus

  val viewModel: MainActivityViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Turn off the decor fitting system windows, which allows us to handle insets,
    // including IME animations
    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      val systemUiController = rememberSystemUiController()
      val darkTheme = false

      // Update the dark content of the system bars to match the theme
      DisposableEffect(systemUiController, darkTheme) {
        systemUiController.systemBarsDarkContentEnabled = !darkTheme
        onDispose {}
      }

      ExpensesTrackerTheme {
        ExpeApp(
          windowSizeClass = calculateWindowSizeClass(this),
          topAppBarActionClickEventBus = topAppBarActionClickEventBus,
        )
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(
    text = "Hello $name!",
    modifier = modifier
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ExpensesTrackerTheme {
    Greeting("Android")
  }
}