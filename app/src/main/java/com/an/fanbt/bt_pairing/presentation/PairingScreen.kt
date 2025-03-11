package com.an.fanbt.bt_pairing.presentation

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.an.fanbt.bt_pairing.domain.PairingEvent
import com.an.fanbt.core.Screen


@Composable
fun PairingScreen(
    navController: NavController,
    viewModel: PairingViewModel
) {

    val context = LocalContext.current

    val state = viewModel
        .state
        .collectAsState()
        .value

    val event = viewModel
        .pairingEvent
        .collectAsState(initial = null).value

    val enableBtLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
        val isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions[Manifest.permission.BLUETOOTH_CONNECT] ?: false &&
                    permissions[Manifest.permission.BLUETOOTH_SCAN] ?: false
        } else {
            permissions[Manifest.permission.BLUETOOTH] ?: false &&
                    permissions[Manifest.permission.BLUETOOTH_ADMIN] ?: false
        }
        viewModel.setPermissionStatus(isGranted)
        }

    LaunchedEffect(event) {

        when(event) {
            is PairingEvent.Error -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                viewModel.cancelJob()

            }
            PairingEvent.NavigateToFanControl -> {
                navController.navigate(Screen.Dashboard.route)
            }
            PairingEvent.CheckBTPermission -> {
                val isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.BLUETOOTH_SCAN
                            ) == PackageManager.PERMISSION_GRANTED
                } else {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH
                    ) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.BLUETOOTH_ADMIN
                            ) == PackageManager.PERMISSION_GRANTED
                }
                viewModel.setPermissionStatus(isGranted)
                viewModel.setPermissionStatus(isGranted)
            }
            PairingEvent.AskForPermission -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN
                        )
                    )
                } else {
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN
                        )
                    )
                }
            }
            else -> {}
        }

    }

    LaunchedEffect(state.isBluetoothEnabled, state.hasBTPermission) {
        state.isBluetoothEnabled?.let { isEnabled ->
            if(!isEnabled && state.hasBTPermission) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBtLauncher.launch(enableBtIntent)
            }
            if(state.hasBTPermission) {
                viewModel.startDiscovery()
            }
        }
    }

    LaunchedEffect(state.connectedDevice) {
        state.connectedDevice?.let { device ->
            Toast.makeText(context, "Connected to: ${device.name}", Toast.LENGTH_SHORT).show()
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                viewModel.updatePairedDevices()

            }) {
                Text(text = "Scan")
            }

            if(state.connectionJob != null && state.connectedDevice == null) {
                CircularProgressIndicator()
            }

        }


        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            item {
                Text(text = "Paired")
            }

            items(state.pairedDevices) { device ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable {
                            Log.d("TAG", "onCreate: click")
                            viewModel.connectToDevice(device)
                        }
                ) {
                    Text(text = device.name)
                    Text(text = device.macAddress)
                }
            }

        }

    }



}