package com.psyduckco.bleh.device

sealed class DeviceScreenEvent {
    object onDeviceConnected: DeviceScreenEvent()
    object onDeviceConnecting : DeviceScreenEvent()
    object onDeviceDisconnecting : DeviceScreenEvent()
    object onDeviceDisconnected: DeviceScreenEvent()
}
