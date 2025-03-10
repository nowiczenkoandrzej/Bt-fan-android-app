package com.an.fanbt.dashboard.domain

sealed class LoginEvent {

    object Error: LoginEvent()
    object Success: LoginEvent()

}