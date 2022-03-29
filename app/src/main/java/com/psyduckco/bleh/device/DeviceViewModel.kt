package com.psyduckco.bleh.device

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class DeviceViewModel : ViewModel() {

    private val _contentState = MutableLiveData(DeviceScreenState.CONNECTING)
    val contentState: LiveData<DeviceScreenState> = _contentState


    private val _services = MutableLiveData(ArrayList<BluetoothGattService>())
    val services: LiveData<ArrayList<BluetoothGattService>> = _services


    internal var isConnectedState = false;

    fun updateContentState(newContentState: DeviceScreenState) {
        _contentState.postValue(newContentState)
    }

    fun addNewService(foundService: BluetoothGattService) {
        val tempList: ArrayList<BluetoothGattService>? = _services.value
        var alreadyFound = false

        if(tempList != null) {
            tempList.forEach {
                if(it.uuid.equals(foundService.uuid)) {
                    alreadyFound = true
                }
            }
            if(!alreadyFound) {
                tempList.add(foundService)
            }
        }

        _services.postValue(tempList)
    }
}