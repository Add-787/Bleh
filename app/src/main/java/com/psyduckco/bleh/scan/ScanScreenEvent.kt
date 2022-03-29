package com.psyduckco.bleh.scan

import android.bluetooth.BluetoothDevice

sealed class ScanScreenEvent {
    data class onDeviceSelected(val device: BluetoothDevice) : ScanScreenEvent()
    object onScanStarted: ScanScreenEvent()
    object onScanStopped: ScanScreenEvent()
}
