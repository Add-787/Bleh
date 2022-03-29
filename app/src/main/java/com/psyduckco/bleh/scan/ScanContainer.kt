package com.psyduckco.bleh.scan

import android.bluetooth.BluetoothDevice

interface ScanContainer {
    fun onDeviceClicked(device: BluetoothDevice)
}