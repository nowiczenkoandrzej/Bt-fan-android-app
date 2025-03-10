package com.an.fanbt.dashboard.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.an.fanbt.bluetooth.data.BluetoothController
import com.an.fanbt.bluetooth.data.listen
import com.an.fanbt.bluetooth.domain.BtDevice
import com.an.fanbt.bluetooth.domain.ConnectionResult
import com.an.fanbt.core.SpeedMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.net.Inet4Address
import javax.inject.Inject

class FanCommunicationRepository @Inject constructor(
    private val bluetoothController: BluetoothController,
) {

    fun pairWithBluetooth(device: BtDevice) {
        bluetoothController.connectToDevice(device)
    }


    suspend fun setMode(mode: SpeedMode) {
        bluetoothController.trySendMessage(mode)
    }




}