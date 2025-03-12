package com.an.fanbt

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FanApp: Application() {
    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            "control_channel",
            "Fan Control",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)

    }
}
