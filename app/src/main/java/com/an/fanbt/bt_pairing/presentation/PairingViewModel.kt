package com.an.fanbt.bt_pairing.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.an.fanbt.bluetooth.data.BluetoothController
import com.an.fanbt.bluetooth.data.listen
import com.an.fanbt.bluetooth.domain.BtDevice
import com.an.fanbt.bluetooth.domain.ConnectionResult
import com.an.fanbt.bt_pairing.domain.PairingEvent
import com.an.fanbt.bt_pairing.domain.PairingScreenState
import com.an.fanbt.dashboard.data.FanCommunicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PairingViewModel @Inject constructor(
    private val btController: BluetoothController,
    private val fanCommunicationRepository: FanCommunicationRepository
): ViewModel() {


    private var connectionJob = MutableStateFlow<Job?>(null)

    private val connectedDevice = MutableStateFlow<BtDevice?>(null)

    private val hasBtPermission = MutableStateFlow<Boolean>(false)

    private val _pairingEvent = Channel<PairingEvent>()
    val pairingEvent= _pairingEvent.receiveAsFlow()


    val state: StateFlow<PairingScreenState> = combine(
        btController.pairedDevices,
        btController.isEnabled,
        connectedDevice,
        connectionJob,
        hasBtPermission
    ) { pairedDevices, isEnabled, device, job, isGranted ->
        PairingScreenState(
            isBluetoothEnabled = isEnabled,
            pairedDevices = pairedDevices.filter { device ->
                device.name.contains("Esp Fan")
            },
            connectedDevice = device,
            connectionJob = job,
            hasBTPermission = isGranted
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PairingScreenState())

    init {
        viewModelScope.launch {
            _pairingEvent.send(PairingEvent.CheckBTPermission)
            delay(300)
            if(!state.value.hasBTPermission)
                _pairingEvent.send(PairingEvent.AskForPermission)
        }
    }

    fun startDiscovery() = btController.startDiscovery()

    fun connectToDevice(device: BtDevice) {
        if(!state.value.hasBTPermission) {
            viewModelScope.launch {
                _pairingEvent.send(PairingEvent.AskForPermission)

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
        if(!state.value.hasBTPermission) {
            viewModelScope.launch {
                _pairingEvent.send(PairingEvent.AskForPermission)

            }
            return
        }

        btController.updatePairedDevices()
    }

    fun cancelJob() {
        connectionJob.value = null
    }

    fun setPermissionStatus(isGranted: Boolean) {
        hasBtPermission.value = isGranted
    }




}