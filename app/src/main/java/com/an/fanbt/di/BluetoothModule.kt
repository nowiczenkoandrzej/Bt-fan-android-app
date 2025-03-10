package com.an.fanbt.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.an.fanbt.bluetooth.data.BluetoothController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    @Provides
    @Singleton
    fun provideBluetoothAdapter(
        @ApplicationContext context: Context
    ): BluetoothAdapter {
        return context
            .getSystemService(BluetoothManager::class.java)
            .adapter
    }

    @Provides
    @Singleton
    fun provideBluetoothController(
        @ApplicationContext context: Context,
        bluetoothAdapter: BluetoothAdapter
    ): BluetoothController {
        return BluetoothController(bluetoothAdapter, context)
    }
}