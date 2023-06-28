package com.emendo.expensestracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.emendo.expensestracker.ui.ExpeApp
import com.emendo.expensestracker.ui.theme.ExpensesTrackerTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Turn off the decor fitting system windows, which allows us to handle insets,
    // including IME animations
    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      ExpensesTrackerTheme {
        ExpeApp(
          windowSizeClass = calculateWindowSizeClass(this),
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