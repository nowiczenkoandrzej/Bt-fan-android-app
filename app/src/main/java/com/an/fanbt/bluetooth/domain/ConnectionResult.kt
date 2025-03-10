package com.an.fanbt.bluetooth.domain

sealed interface ConnectionResult {

    data class ConnectionEstablished(val device: BtDevice): ConnectionResult
    data class Error(val message: String): ConnectionResult
    data class TransferSucceeded(val message: String): ConnectionResult

}