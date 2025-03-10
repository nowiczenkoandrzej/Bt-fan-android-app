package com.an.fanbt.dashboard.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.fanbt.bluetooth.data.listen
import com.an.fanbt.dashboard.data.FanCommunicationRepository
import com.an.fanbt.dashboard.domain.LoginEvent
import com.an.fanbt.dashboard.domain.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val fanCommunicationRepository: FanCommunicationRepository
): ViewModel() {

    private val _loginEvent = Channel<LoginEvent>()
    val loginEvent = _loginEvent.receiveAsFlow()

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun typeSSID(ssid: String) {
        _state.value = state.value.copy(
            ssid = ssid
        )
    }

    fun typePassword(password: String) {
        _state.value = state.value.copy(
            password = password
        )
    }



}