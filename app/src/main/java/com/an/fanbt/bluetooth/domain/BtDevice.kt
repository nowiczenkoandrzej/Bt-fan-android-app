package com.an.fanbt.bluetooth.domain

import java.util.UUID

data class BtDevice(
    val name: String = "(No name)",
    val macAddress: String,
    val uuid: String
)
