package com.psyduckco.bleh.device

import android.bluetooth.*
import android.content.Context
import android.util.Log
import com.psyduckco.bleh.common.BaseLogic
import com.psyduckco.bleh.common.DispatcherProvider
import com.psyduckco.bleh.scan.ScanViewModel
import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.schedule
import kotlin.coroutines.CoroutineContext

class DeviceLogic(
    private val container: DeviceContainer?,
    private val device: BluetoothDevice?,
    private val context: Context,
    private val viewModel: DeviceViewModel,
    private val dispatcher: DispatcherProvider,
) : BaseLogic<DeviceScreenEvent>(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker

    init {
        jobTracker = Job()
    }

    private val TAG ="DeviceLogic"

    private val bleManager: BluetoothManager = context.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private val bleAdapter: BluetoothAdapter?
        get() = bleManager.adapter

    private var bleGatt: BluetoothGatt? = null

    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            if(newState == BluetoothProfile.STATE_CONNECTED) {
                connectedToDevice()
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            gatt?.services?.forEach { it ->
                viewModel.addNewService(it)
            }

        }


    }


    override fun onEvent(event: DeviceScreenEvent) {
        when(event) {
            DeviceScreenEvent.onDeviceConnected -> connectedToDevice()
            DeviceScreenEvent.onDeviceConnecting -> connectToDevice()
            DeviceScreenEvent.onDeviceDisconnected -> navigateToScanScreen()
            DeviceScreenEvent.onDeviceDisconnecting -> disconnectFromDevice()
        }
    }

    private fun connectedToDevice() {
        viewModel.isConnectedState = true
        viewModel.updateContentState(DeviceScreenState.CONNECTED)
        bleGatt?.discoverServices()
    }

    private fun navigateToScanScreen() {
        disconnectFromDevice()
        container?.onDeviceDisconnected()
    }

    fun disconnectFromDevice() {
        bleGatt?.disconnect()
        bleGatt?.close()
        viewModel.isConnectedState = false
        viewModel.updateContentState(DeviceScreenState.DISCONNECTED)
    }

    private fun disconnectDeviceAfterDelay(millis: Long) = launch {
        Timer("AutomaticDisconnect",false).schedule(millis) {
            if(!viewModel.isConnectedState) {
                disconnectFromDevice()
            }
        }
    }

    private fun connectToDevice() {
        bleGatt = device?.connectGatt(context,false, gattCallback)
        disconnectDeviceAfterDelay(4000)
    }
}