package com.an.fanbt


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.an.fanbt.core.Navigation
import com.an.fanbt.ui.theme.FanBTTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FanBTTheme {

                val navController = rememberNavController()

                Navigation(navController = navController)


            }
        }
    }

}
