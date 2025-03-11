package com.an.fanbt.dashboard.data

import com.an.fanbt.bluetooth.data.BluetoothController
import com.an.fanbt.bluetooth.domain.BtDevice
import com.an.fanbt.core.SpeedMode
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