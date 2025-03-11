package com.an.fanbt.bluetooth.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewModelScope
import com.an.fanbt.bluetooth.domain.ConnectionResult
import com.an.fanbt.bluetooth.domain.BtDevice
import com.an.fanbt.bt_pairing.domain.PairingEvent
import com.an.fanbt.core.SpeedMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothController(
    private val bluetoothAdapter: BluetoothAdapter?,
    private val context: Context
) {

    private var currentClientSocket: BluetoothSocket? = null

    private var dataTransferService: BluetoothDataTransferService? = null

    private val _pairedDevices = MutableStateFlow<List<BtDevice>>(emptyList())
    val pairedDevices = _pairedDevices.asStateFlow()

    private val _isConnected = MutableStateFlow(false)

    private val _isEnabled = MutableStateFlow(false)
    val isEnabled = _isEnabled.asStateFlow()

    private val _errors = MutableSharedFlow<String>()



    private val btConnectionStateReceiver = BtConnectionStateReceiver { isConnected, device ->

        if(bluetoothAdapter?.bondedDevices?.contains(device) == true) {
            _isConnected.update { isConnected }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Can't connect to device")
            }
        }
    }

    private val btEnableStateReceiver = BtEnableStateReceiver { isEnabled ->
        _isEnabled.value = isEnabled
    }


    init {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startDiscovery()
        }
    }

    fun startDiscovery() {
        updatePairedDevices()
        context.registerReceiver(
            btConnectionStateReceiver,
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
        )

        context.registerReceiver(
            btEnableStateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }

    fun connectToDevice(device: BtDevice): Flow<ConnectionResult> {
        return flow {

            val btDevice = bluetoothAdapter?.getRemoteDevice(device.macAddress)

            currentClientSocket = btDevice
                ?.createRfcommSocketToServiceRecord(
                    UUID.fromString(device.uuid)
                )
            stopDiscovery()

            currentClientSocket?.let { socket ->
                try {
                    socket.connect()
                    emit(ConnectionResult.ConnectionEstablished(device))

                    BluetoothDataTransferService(socket).also {
                        dataTransferService = it
                        emitAll(
                            it.listenForIncomingMessages()
                                .map {
                                    ConnectionResult.TransferSucceeded(it)
                                }
                        )
                    }

                } catch (e: IOException) {
                    socket.close()
                    currentClientSocket = null
                    emit(ConnectionResult.Error("Connection was interrupted"))
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun trySendMessage(mode: SpeedMode) {
        if(dataTransferService == null) {
            return
        }

        val message = when(mode){
            SpeedMode.SLOW -> BTMode.SLOW
            SpeedMode.MEDIUM -> BTMode.MEDIUM
            SpeedMode.FAST -> BTMode.FAST
            SpeedMode.STOP -> BTMode.STOP
        }

        dataTransferService!!.sendMessage(message.toByteArray())
    }


    fun closeConnection() {
        currentClientSocket?.close()
        currentClientSocket = null
    }


    fun stopDiscovery() { bluetoothAdapter?.cancelDiscovery() }

    fun release() {
        context.unregisterReceiver(btConnectionStateReceiver)
        context.unregisterReceiver(btEnableStateReceiver)

        closeConnection()
    }
    
    fun updatePairedDevices() {
        bluetoothAdapter
            ?.bondedDevices
            ?.map { BtDevice(
                name = it.name,
                macAddress = it.address,
                uuid = it.uuids[0].toString()
            ) }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

}

fun Flow<ConnectionResult>.listen(
    onError: (String) -> Unit,
    onConnectionEstablished: (BtDevice) -> Unit = {},
    onTransferSucceeded: (String) -> Unit,
    scope: CoroutineScope
): Job {
    return onEach { result ->
        when(result) {
            is ConnectionResult.Error -> {
                onError("Unable to Connect")
            }
            is ConnectionResult.ConnectionEstablished -> {
                onConnectionEstablished(result.device)
            }
            is ConnectionResult.TransferSucceeded -> {
                onTransferSucceeded(result.message)
            }
        }
    }.catch { throwable ->
        throwable.message?.let { onError(throwable.message.toString()) }
    }.launchIn(scope)
}
