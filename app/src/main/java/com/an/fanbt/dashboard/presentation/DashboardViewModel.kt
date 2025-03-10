package com.an.fanbt.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.fanbt.bluetooth.data.BluetoothController
import com.an.fanbt.core.SpeedMode
import com.an.fanbt.dashboard.data.FanCommunicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val btController: BluetoothController,
    private val repository: FanCommunicationRepository,
): ViewModel() {



    fun sendMessage(mode: SpeedMode) {
        viewModelScope.launch {
            repository.setMode(mode)
        }
    }



}