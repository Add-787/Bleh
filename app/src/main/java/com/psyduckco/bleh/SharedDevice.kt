package com.psyduckco.bleh

import android.app.Application
import android.bluetooth.BluetoothDevice

class SharedDevice : Application() {

    private var sharedDevice: BluetoothDevice? = null

    fun getSharedDevice() : BluetoothDevice? = sharedDevice

    fun setSharedDevice(device: BluetoothDevice?) {
        sharedDevice = device
    }

}