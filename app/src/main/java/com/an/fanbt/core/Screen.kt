package com.an.fanbt.core

sealed class Screen(val route: String) {
    object Pairing: Screen(route = "pairing")
    object Dashboard: Screen(route = "fan_control")
}
