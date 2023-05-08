package com.example.clocky.stopwatchservice.notification

import android.app.Notification

/**
 * Interface for building notifications that are meant to show the current state of the
 * stopwatch. Intended to be mainly used with [com.example.clocky.stopwatchservice.StopwatchService].
 */
interface NotificationBuilder {
    /**
     * Builds a [Notification] containing the [currentTimeText] as the content text of the
     * notification.
     */
    fun buildNotification(currentTimeText: String): Notification
}