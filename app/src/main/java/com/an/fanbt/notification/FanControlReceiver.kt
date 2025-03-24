package com.an.fanbt.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.an.fanbt.R
import com.an.fanbt.bluetooth.data.BluetoothController
import com.an.fanbt.core.SpeedMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FanControlReceiver: BroadcastReceiver() {

    @Inject
    lateinit var btManager: BluetoothController


    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            "low_mode" -> {
                updateNotification(context, "Current mode: Low")
                CoroutineScope(Dispatchers.IO).launch {
                    btManager.trySendMessage(SpeedMode.SLOW)
                }
            }
            "medium_mode" -> {
                updateNotification(context, "Current mode: Medium")
                CoroutineScope(Dispatchers.IO).launch {
                    btManager.trySendMessage(SpeedMode.MEDIUM)
                }
            }
            "high_mode" -> {
                updateNotification(context, "Current mode: High")
                CoroutineScope(Dispatchers.IO).launch {
                    btManager.trySendMessage(SpeedMode.FAST)
                }
            }
            "stop_fan" -> {
                updateNotification(context, "Fan stopped")
                CoroutineScope(Dispatchers.IO).launch {
                    btManager.trySendMessage(SpeedMode.STOP)
                }
            }
        }
    }

    private fun updateNotification(context: Context, text: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "fan_control_channel")
            .setContentTitle("Fan Controller")
            .setContentText(text)
            .setSmallIcon(R.drawable.baseline_wind_power_24)
//            .setContentIntent(pendingIntent)
//            .setOngoing(true)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .addAction(lowAction)
//            .addAction(mediumAction)
//            .addAction(highAction)
//            .addAction(stopAction)
            .build()

        notificationManager.notify(1, notification)
    }
}