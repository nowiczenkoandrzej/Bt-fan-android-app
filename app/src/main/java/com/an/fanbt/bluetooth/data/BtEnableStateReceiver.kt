package com.an.fanbt.bluetooth.data

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BtEnableStateReceiver(
    private val onStateChanged: (isEnabled: Boolean) -> Unit
): BroadcastReceiver() {
    override fun onReceive(contex: Context?, intent: Intent?) {

        when(intent?.action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )

                when(state) {
                    BluetoothAdapter.STATE_ON -> {
                        onStateChanged(true)
                    }
                    BluetoothAdapter.STATE_OFF -> {
                        onStateChanged(false)
                    }
                }

            }
        }


    }

}