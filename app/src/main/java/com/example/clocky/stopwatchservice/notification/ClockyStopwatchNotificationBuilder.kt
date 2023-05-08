package com.example.clocky.stopwatchservice.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.clocky.R
import com.example.clocky.ui.MainActivity

/**
 * A concrete implementation of [StopwatchNotificationBuilder].
 */
class ClockyStopwatchNotificationBuilder(
    private val context: Context
) : StopwatchNotificationBuilder {
    /**
     * The intent that would be used for open the activity when the notification is clicked.
     */
    private val onClickIntent = PendingIntent.getActivity(
        context,
        ONCLICK_NOTIFICATION_INTENT_REQUEST_CODE,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    /**
     * An instance of [NotificationCompat.Builder] that sets up the basic characteristics common to
     * every notification.
     */
    private val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_timer_24)
        .setShowWhen(false)
        .setContentTitle("Stopwatch")
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setContentIntent(onClickIntent)

    /**
     * Used to build a [Notification] with the [currentTimeText] set as the content text.
     */
    override fun buildNotification(currentTimeText: String): Notification {
        createNotificationChannelIfNecessary()
        return notificationBuilder.setContentText(currentTimeText).build()
    }

    /**
     * Used to create a notification channel if the version of Android > Oreo.
     */
    private fun createNotificationChannelIfNecessary() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) return
        val notificationChannel = NotificationChannelCompat.Builder(
            NOTIFICATION_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_LOW
        ).setName(NOTIFICATION_CHANNEL_NAME).build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(notificationChannel)

    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"
        private const val NOTIFICATION_CHANNEL_NAME = "Elapsed Time"
        private const val ONCLICK_NOTIFICATION_INTENT_REQUEST_CODE = 10
    }
}
