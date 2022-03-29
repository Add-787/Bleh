package com.psyduckco.bleh.scan

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.util.Log
import com.psyduckco.bleh.common.BaseLogic
import com.psyduckco.bleh.common.DispatcherProvider
import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.schedule
import kotlin.coroutines.CoroutineContext

class ScanLogic(
    private val container: ScanContainer?,
    context: Context,
    private val viewModel: ScanViewModel,
    private val dispatcher: DispatcherProvider,
) : BaseLogic<ScanScreenEvent>(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideIOContext() + jobTracker

    init {
        jobTracker = Job()
    }

    private val TAG ="ScanLogic"

    private val bleManager: BluetoothManager = context.applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager

    private val bleAdapter: BluetoothAdapter?
        get() = bleManager.adapter

    private var bleGatt: BluetoothGatt? = null

    private var isStatusChanged = false

    override fun onEvent(event: ScanScreenEvent) {
        when(event) {
            ScanScreenEvent.onScanStarted -> startScan()
            ScanScreenEvent.onScanStopped -> stopScan()
            is ScanScreenEvent.onDeviceSelected -> navigateToDevice(event.device)
        }
    }

    private fun navigateToDevice(device: BluetoothDevice) {
        container?.onDeviceClicked(device)
    }

    private fun startScan() {
        viewModel.updateStatusMessage("Scan Started!")
        viewModel.toggleScan(true)
        bleAdapter?.bluetoothLeScanner?.startScan(bleScanCallback)
        stopScanAfterDelay(3000)
    }

    private fun stopScanAfterDelay(millis: Long) = launch {
        Timer("AutomaticStopScan",false).schedule(millis) {
            stopScan()
        }
    }

    private fun stopScan() {
        viewModel.updateStatusMessage("Scan Stopped.")
        viewModel.toggleScan(false)
        bleAdapter?.bluetoothLeScanner?.stopScan(bleScanCallback)
    }

    private val bleScanCallback:  ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.let {
                viewModel.addNewDevice(it)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }
    }
}