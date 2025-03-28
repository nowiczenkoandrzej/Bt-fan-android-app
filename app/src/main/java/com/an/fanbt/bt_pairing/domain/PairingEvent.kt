package com.an.fanbt.bt_pairing.domain

sealed class PairingEvent {
    data class Error(val message: String): PairingEvent()
    object NavigateToFanControl: PairingEvent()
    object CheckBTPermission: PairingEvent()
    data class AskForPermission(val missingPermissions: List<String>): PairingEvent()
}
