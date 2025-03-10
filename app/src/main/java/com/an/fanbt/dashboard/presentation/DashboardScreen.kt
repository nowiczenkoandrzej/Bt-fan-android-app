package com.an.fanbt.dashboard.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.an.fanbt.core.Screen
import com.an.fanbt.core.SpeedMode

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {


        Button(onClick = {
            viewModel.sendMessage(SpeedMode.SLOW)
        }) {
            Text(text = "1")
        }
        Button(onClick = {
            viewModel.sendMessage(SpeedMode.MEDIUM)
        }) {
            Text(text = "2")
        }
        Button(onClick = {
            viewModel.sendMessage(SpeedMode.FAST)
        }) {
            Text(text = "3")
        }
        Button(onClick = {
            viewModel.sendMessage(SpeedMode.STOP)
        }) {
            Text(text = "X")
        }


    }


}