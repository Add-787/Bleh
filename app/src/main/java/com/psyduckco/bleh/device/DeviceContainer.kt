package com.psyduckco.bleh.device

interface DeviceContainer {
    fun onDeviceDisconnected()
    fun showError(message: String)
}