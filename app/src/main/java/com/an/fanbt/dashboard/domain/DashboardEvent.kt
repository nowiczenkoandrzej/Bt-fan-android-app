package com.an.fanbt.dashboard.domain

import com.an.fanbt.core.SpeedMode

sealed class DashboardEvent {
    data class SetSpeedMode(val speedMode: SpeedMode): DashboardEvent()
    object startNotification: DashboardEvent()
}