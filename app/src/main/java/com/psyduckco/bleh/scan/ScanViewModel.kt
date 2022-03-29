package com.psyduckco.bleh.scan

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ScanViewModel : ViewModel() {

    private val _devices = MutableLiveData(ArrayList<BluetoothDevice>())
    val devices: LiveData<ArrayList<BluetoothDevice>> = _devices

    private val _statusMsg = MutableLiveData("Welcome")
    val statusMsg: LiveData<String> = _statusMsg

    private val _isScanning = MutableLiveData(false)
    val isScanning: LiveData<Boolean> = _isScanning

    private val _noOfDevices = MutableLiveData(0)
    val noOfDevices: LiveData<Int> = _noOfDevices

    fun updateStatusMessage(msg: String) {
        _statusMsg.postValue(msg)
    }

    fun toggleScan(isScanning: Boolean) {
        _isScanning.postValue(isScanning)
    }

    fun addNewDevice(foundDevice: BluetoothDevice) {
        _devices.value?.forEach { device ->
            if(device.address == foundDevice.address) return
        }
        _devices.value?.add(foundDevice)
        _noOfDevices.value = _noOfDevices.value?.plus(1)
    }

}