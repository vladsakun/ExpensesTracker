package com.emendo.expensestracker

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.navigation.compose.rememberNavController
import com.emendo.expensestracker.accounts.destinations.AccountDetailScreenDestination
import com.emendo.expensestracker.broadcast.LocaleBroadcastReceiver
import com.emendo.expensestracker.core.app.base.app.base.TimeZoneBroadcastReceiver
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.ui.ExpeApp
import com.emendo.expensestracker.ui.rememberExpeAppState
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val viewModel: MainActivityViewModel by viewModels()

  private val localeBroadcastReceiver by lazy { LocaleBroadcastReceiver(viewModel::updateLocale) }
  private val timeZoneBroadcastReceiver by lazy { TimeZoneBroadcastReceiver(viewModel::updateTimeZone) }

  override fun onCreate(savedInstanceState: Bundle?) {
    // Turn off the decor fitting system windows, which allows us to handle insets,
    // including IME animations, and go edge-to-edge
    // This also sets up the initial system bar style based on the platform theme
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    setContent {
      val darkTheme = isSystemInDarkTheme()
      val navController = rememberNavController()
      val appState = rememberExpeAppState(
        windowSizeClass = calculateWindowSizeClass(this),
        navController = navController,
      )

      ExpensesTrackerTheme(darkTheme = darkTheme) {
        ExpeApp(appState)
        ProtectNavigationBar()
      }
    }
  }

  @Composable
  private fun ProtectNavigationBar(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val tappableElement = WindowInsets.tappableElement
    val bottomPixels = tappableElement.getBottom(density)
    val usingTappableBars = remember(bottomPixels) {
      bottomPixels != 0
    }
    val barHeight = remember(bottomPixels) {
      tappableElement.asPaddingValues(density).calculateBottomPadding()
    }

    Column(
      modifier = modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Bottom
    ) {
      if (usingTappableBars) {
        Box(
          modifier = Modifier
            .background(BottomAppBarDefaults.containerColor.copy(alpha = 0.5f))
            .fillMaxWidth()
            .height(barHeight)
        )
      }
    }
  }

  override fun onStart() {
    super.onStart()
    localeBroadcastReceiver.register(this)
    timeZoneBroadcastReceiver.register(this)

    displayNotification(
      title = "Test deep link",
      text = "Profile screen deep link",
      notificationID = 100,
      channel = "DEFAULT",
      pendingIntent = getPendingIntent()
    )
  }

  override fun onStop() {
    super.onStop()
    localeBroadcastReceiver.unregister(this)
    timeZoneBroadcastReceiver.unregister(this)
  }

  private fun getPendingIntent(): PendingIntent {
    val deepLinkPrefix = "https://emendo.com"
    val profileDeepLink = "$deepLinkPrefix/accounts"
    val profileDetailsDeepLink = "$deepLinkPrefix/accounts/${AccountDetailScreenDestination(accountId = 1L).route}"
    val deepLinkTest = "$deepLinkPrefix/test"

    return TaskStackBuilder.create(applicationContext).run {
      addNextIntentWithParentStack(
        Intent(
          Intent.ACTION_VIEW,
          profileDetailsDeepLink.toUri(),
          applicationContext,
          MainActivity::class.java
        )
      )
      getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
    }
  }

  @SuppressLint("UnspecifiedImmutableFlag")
  @Suppress("SameParameterValue")
  private fun displayNotification(
    title: String,
    text: String,
    notificationID: Int,
    channel: String,
    pendingIntent: PendingIntent?,
  ) {
    val notificationManager = applicationContext
      .getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    val defaultChannel =
      NotificationChannel(
        "DEFAULT",
        "DEFAULT",
        NotificationManager.IMPORTANCE_DEFAULT
      )
    defaultChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    notificationManager.createNotificationChannel(defaultChannel)
    val builder = NotificationCompat.Builder(
      applicationContext,
      channel
    )
    builder.setContentTitle(title)
    builder.setTicker(title)
    builder.setContentText(text)
    builder.setSmallIcon(R.drawable.ic_launcher_background)

    builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS or NotificationCompat.DEFAULT_VIBRATE)
    val notificationIntent = Intent(applicationContext, MainActivity::class.java)
    builder.setContentIntent(
      pendingIntent ?: PendingIntent.getActivity(
        applicationContext,
        0,
        notificationIntent,
        0
      )
    )
    val notification = builder.build()

    //Dismiss the notification on tap
    notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

    //Update the proper notification
    notificationManager.notify(notificationID, notification)
  }

  private suspend fun logCompilationStatus() {
    //    withContext(Dispatchers.IO) {
    //      val status = ProfileVerifier.getCompilationStatusAsync()
    //      when (status.profileInstallResultCode) {
    //        RESULT_CODE_NO_PROFILE ->
    //          Log.d(TAG, "ProfileInstaller: Baseline Profile not found")
    //
    //        RESULT_CODE_COMPILED_WITH_PROFILE ->
    //          Log.d(TAG, "ProfileInstaller: Compiled with profile")
    //
    //        RESULT_CODE_PROFILE_ENQUEUED_FOR_COMPILATION ->
    //          Log.d(TAG, "ProfileInstaller: Enqueued for compilation")
    //
    //        RESULT_CODE_COMPILED_WITH_PROFILE_NON_MATCHING ->
    //          Log.d(TAG, "ProfileInstaller: App was installed through Play store")
    //
    //        RESULT_CODE_ERROR_PACKAGE_NAME_DOES_NOT_EXIST ->
    //          Log.d(TAG, "ProfileInstaller: PackageName not found")
    //
    //        RESULT_CODE_ERROR_CACHE_FILE_EXISTS_BUT_CANNOT_BE_READ ->
    //          Log.d(TAG, "ProfileInstaller: Cache file exists but cannot be read")
    //
    //        RESULT_CODE_ERROR_CANT_WRITE_PROFILE_VERIFICATION_RESULT_CACHE_FILE ->
    //          Log.d(TAG, "ProfileInstaller: Can't write cache file")
    //
    //        RESULT_CODE_ERROR_UNSUPPORTED_API_VERSION ->
    //          Log.d(TAG, "ProfileInstaller: Enqueued for compilation")
    //
    //        else ->
    //          Log.d(TAG, "ProfileInstaller: Profile not compiled or enqueued")
    //      }
    //    }
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
}