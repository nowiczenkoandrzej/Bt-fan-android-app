package com.an.fanbt.core

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.an.fanbt.dashboard.presentation.DashboardScreen
import com.an.fanbt.dashboard.presentation.DashboardViewModel
import com.an.fanbt.bt_pairing.presentation.PairingScreen
import com.an.fanbt.bt_pairing.presentation.PairingViewModel

@Composable
fun Navigation(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Pairing.route
    ) {
        composable(route = Screen.Pairing.route) {
            val viewModel = hiltViewModel<PairingViewModel>()

            PairingScreen(
                navController = navController,
                viewModel = viewModel
            )

        }

        composable(route = Screen.Dashboard.route) {
            val viewModel = hiltViewModel<DashboardViewModel>()

            DashboardScreen(
                navController = navController,
                viewModel = viewModel
            )
        }



    }

}