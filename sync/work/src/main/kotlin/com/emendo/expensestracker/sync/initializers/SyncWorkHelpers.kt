package com.emendo.expensestracker.sync.initializers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import com.emendo.expensestracker.core.app.resources.R as AppR

const val SYNC_TOPIC = "sync"
private const val SYNC_NOTIFICATION_ID = 0
private const val SYNC_NOTIFICATION_CHANNEL_ID = "SyncNotificationChannel"

// All sync work needs an internet connection
val SyncConstraints
  get() = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

/**
 * Foreground information for sync on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.syncForegroundInfo() = ForegroundInfo(
  SYNC_NOTIFICATION_ID,
  syncWorkNotification(),
)

/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
private fun Context.syncWorkNotification(): Notification {
  val channel = NotificationChannel(
    SYNC_NOTIFICATION_CHANNEL_ID,
    getString(AppR.string.sync_notification_channel_name),
    NotificationManager.IMPORTANCE_DEFAULT,
  ).apply {
    description = getString(AppR.string.sync_notification_channel_description)
  }
  // Register the channel with the system
  val notificationManager: NotificationManager? =
    getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

  notificationManager?.createNotificationChannel(channel)

  return NotificationCompat.Builder(this, SYNC_NOTIFICATION_CHANNEL_ID)
    // Todo change this icon
    .setSmallIcon(androidx.core.R.drawable.notification_bg)
    .setContentTitle(getString(AppR.string.sync_notification_title))
    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    .build()
}