package com.an.fanbt.bt_pairing.domain

import com.an.fanbt.bluetooth.domain.BtDevice
import kotlinx.coroutines.Job

data class PairingScreenState(
    val isBluetoothEnabled: Boolean? = null,
    val pairedDevices: List<BtDevice> = emptyList(),
    val connectedDevice: BtDevice? = null,
    val connectionJob: Job? = null,
    val hasBTPermission: Boolean = false
)
