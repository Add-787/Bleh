package com.psyduckco.bleh.scan

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.psyduckco.bleh.SharedDevice
import com.psyduckco.bleh.common.ProductionDispatcherProvider
import com.psyduckco.bleh.device.DeviceActivity
import com.psyduckco.bleh.ui.theme.BlehTheme

class ScanActivity : AppCompatActivity(), ScanContainer {
    private lateinit var logic: ScanLogic

    @ExperimentalPermissionsApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this).get(ScanViewModel::class.java)

        setContent {
            BlehTheme {
                ScanScreen(
                    logic::onEvent,
                    viewModel
                )
            }
        }

        logic = buildScanLogic(this,viewModel, applicationContext)

    }

    override fun onBackPressed() {
        super.onBackPressed()
            finish()
    }

    override fun onDeviceClicked(device: BluetoothDevice) {

        val app : SharedDevice = application as SharedDevice
        app.setSharedDevice(device)

        startActivity(
            Intent(
                this,
                DeviceActivity::class.java
            )
        )
    }

}

internal fun buildScanLogic(container: ScanContainer, viewModel: ScanViewModel, context: Context) : ScanLogic {
    return ScanLogic(container,context, viewModel, ProductionDispatcherProvider)
}

