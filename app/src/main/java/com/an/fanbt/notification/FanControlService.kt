package com.an.fanbt.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.an.fanbt.MainActivity
import com.an.fanbt.R
import com.an.fanbt.core.SpeedMode

class FanControlService: Service() {

    private val channelId = "fan_control_channel"

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Fan Control",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for fan modes"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val lowAction = createAction("Low", "low_mode")
        val mediumAction = createAction("Medium", "medium_mode")
        val highAction = createAction("High", "high_mode")
        val stopAction = createAction("Stop", "stop_fan")

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Fan Controller")
            .setContentText("Fan stopped")
            .setSmallIcon(R.drawable.baseline_wind_power_24)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(lowAction)
            .addAction(mediumAction)
            .addAction(highAction)
            .addAction(stopAction)
            .build()
    }


    private fun createAction(text: String, action: String): NotificationCompat.Action {
        val intent = Intent(this, FanControlReceiver::class.java).apply {
            this.action = action


        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Action.Builder(
            null, // Ikona - możesz dodać jeśli chcesz
            text,
            pendingIntent
        ).build()
    }

}