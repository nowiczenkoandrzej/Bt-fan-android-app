package com.an.fanbt.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


class PermissionManager @Inject constructor(
    private val context: Context
) {
    private val _permissions = MutableStateFlow(PermissionState())
    val permissions = _permissions.asStateFlow()

    init {
        updatePermissions()
    }

    fun updatePermissions() {
        _permissions.value = PermissionState(
            hasBtConnectPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED,
            hasBtScanPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED,
            hasPostNotificationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED,
        )
    }

    fun getMissingPermissions(): List<String> {
        val result = mutableListOf<String>()

        if(!permissions.value.hasBtConnectPermission)
            result.add(Manifest.permission.BLUETOOTH_CONNECT)
        if(!permissions.value.hasBtScanPermission)
            result.add(Manifest.permission.BLUETOOTH_SCAN)
        if(!permissions.value.hasPostNotificationPermission)
            result.add(Manifest.permission.POST_NOTIFICATIONS)

        return result
    }


}

data class PermissionState(
    val hasBtScanPermission: Boolean = false,
    val hasBtConnectPermission: Boolean = false,
    val hasPostNotificationPermission: Boolean = false
)