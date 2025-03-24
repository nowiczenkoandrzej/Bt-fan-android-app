package com.an.fanbt.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.fanbt.bluetooth.data.BluetoothController
import com.an.fanbt.core.PermissionManager
import com.an.fanbt.core.SpeedMode
import com.an.fanbt.dashboard.domain.DashboardEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val btController: BluetoothController,
    private val permissionManager: PermissionManager
): ViewModel() {


    private val _dashboardEvent = Channel<DashboardEvent>()
    val dashboardEvent = _dashboardEvent.receiveAsFlow()


    init {
        val hasNotificationPermission = permissionManager
            .permissions
            .value
            .hasPostNotificationPermission

        if (hasNotificationPermission) {
            viewModelScope.launch {
                _dashboardEvent.send(DashboardEvent.startNotification)
            }
        }
    }


    fun sendMessage(mode: SpeedMode) {
        viewModelScope.launch {
            btController.trySendMessage(mode)
        }
    }



}