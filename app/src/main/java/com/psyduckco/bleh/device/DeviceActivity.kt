package com.psyduckco.bleh.device

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.psyduckco.bleh.SharedDevice
import com.psyduckco.bleh.common.DispatcherProvider
import com.psyduckco.bleh.common.ProductionDispatcherProvider
import com.psyduckco.bleh.common.makeToast
import com.psyduckco.bleh.ui.theme.BlehTheme

class DeviceActivity : AppCompatActivity(), DeviceContainer {

    private lateinit var logic: DeviceLogic

    lateinit var app : SharedDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = application as SharedDevice
        val device: BluetoothDevice? = app.getSharedDevice()

        val viewModel = ViewModelProvider(this).get(DeviceViewModel::class.java)

        setContent {
            BlehTheme {
                DeviceScreen(logic::onEvent, viewModel = viewModel)
            }
        }

        logic = buildDeviceLogic(this, device, applicationContext, viewModel)
    }

    override fun onDeviceDisconnected() {
        app.setSharedDevice(null)
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        app.setSharedDevice(null)
        logic.disconnectFromDevice()
    }

    override fun showError(message: String) {
        makeToast(message)
    }
}

internal fun buildDeviceLogic(container: DeviceContainer,
                              device: BluetoothDevice?,
                              context: Context,
                              viewModel: DeviceViewModel)
= DeviceLogic(container,device,context, viewModel,ProductionDispatcherProvider)


