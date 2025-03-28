package com.an.fanbt.bt_pairing.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.fanbt.bluetooth.data.BluetoothController
import com.an.fanbt.bluetooth.data.listen
import com.an.fanbt.bluetooth.domain.BtDevice
import com.an.fanbt.bt_pairing.domain.PairingEvent
import com.an.fanbt.bt_pairing.domain.PairingScreenState
import com.an.fanbt.core.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PairingViewModel @Inject constructor(
    private val btController: BluetoothController,
    private val permissionManager: PermissionManager
): ViewModel() {


    private var connectionJob = MutableStateFlow<Job?>(null)

    private val connectedDevice = MutableStateFlow<BtDevice?>(null)


    private val _pairingEvent = Channel<PairingEvent>()
    val pairingEvent = _pairingEvent.receiveAsFlow()

    val permissions = permissionManager.permissions


    val state: StateFlow<PairingScreenState> = combine(
        btController.pairedDevices,
        btController.isEnabled,
        connectedDevice,
        connectionJob,
    ) { pairedDevices, isEnabled, device, job ->
        PairingScreenState(
            isBluetoothEnabled = isEnabled,
            pairedDevices = pairedDevices.filter { device ->
                device.name.contains("Esp Fan")
            },
            connectedDevice = device,
            connectionJob = job,

        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PairingScreenState())

    init {
        viewModelScope.launch {
            _pairingEvent.send(PairingEvent.CheckBTPermission)
            delay(300)
            if(!permissions.value.hasBtConnectPermission)
                _pairingEvent.send(PairingEvent.AskForPermission(
                    permissionManager.getMissingPermissions()
                ))
        }
    }

    fun startDiscovery() = btController.startDiscovery()

    fun connectToDevice(device: BtDevice) {
        if(!permissions.value.hasBtConnectPermission) {
            viewModelScope.launch {
                _pairingEvent.send(PairingEvent.AskForPermission(
                    permissionManager.getMissingPermissions()
                ))
            }
            return
        }

        connectionJob.value?.cancel()
        connectionJob.value = btController.connectToDevice(device).listen(
            onError =  { message ->
                viewModelScope.launch {
                    _pairingEvent.send(PairingEvent.Error(message))
                }
            },
            onConnectionEstablished =  { device ->
                viewModelScope.launch {
                    connectedDevice.value = device
                    _pairingEvent.send(PairingEvent.NavigateToFanControl)
                }
            },
            onTransferSucceeded = { message ->
                Log.d("TAG", "listen: $message")
            },
            scope = viewModelScope
        )

    }

    fun updatePairedDevices() {
        if(!permissions.value.hasBtConnectPermission) {
            viewModelScope.launch {
                _pairingEvent.send(PairingEvent.AskForPermission(
                    permissionManager.getMissingPermissions()
                ))

            }
            return
        }

        btController.updatePairedDevices()
    }

    fun cancelJob() {
        connectionJob.value = null
    }

    fun updatePermissions() {
        permissionManager.updatePermissions()
    }




}