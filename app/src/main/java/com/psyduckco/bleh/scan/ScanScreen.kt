package com.psyduckco.bleh.scan

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.psyduckco.bleh.R

@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@Composable
fun ScanScreen(
    onEventHandler: (ScanScreenEvent) -> Unit,
    viewModel: ScanViewModel,
) {

    val isScanning by viewModel.isScanning.observeAsState(false)

    val devices = viewModel.devices.observeAsState(null)

    val status = viewModel.statusMsg.observeAsState(initial = "Welcome")

    val noOfDevices = viewModel.noOfDevices.observeAsState(initial = 0)

    val permissionsState = rememberMultiplePermissionsState(permissions =
    listOf(Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.BLUETOOTH)
    )

    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            ResultRow(onEventHandler = onEventHandler,
                isScanning = isScanning,
                noOfDevices = noOfDevices.value,
                permissionsGranted = permissionsState.allPermissionsGranted
            )
            Divider(
                thickness = 1.dp,
                color = Color.DarkGray,
                modifier = Modifier.alpha(0.50f)
            )
            devices.value?.let { DevicesGrid(onEventHandler = onEventHandler,devices = it) }
        }
    }

}


@Composable
fun ScanButton(
    onEventHandler: (ScanScreenEvent) -> Unit,
    isScanning: Boolean,
    permissionsGranted: Boolean
) {

    val backGroundColor = if(!permissionsGranted) Color.LightGray else MaterialTheme.colors.secondary

    FloatingActionButton(
        onClick = {
            if(permissionsGranted) {
                if (isScanning) onEventHandler.invoke(ScanScreenEvent.onScanStopped)
                else onEventHandler.invoke(ScanScreenEvent.onScanStarted)
            }
        },
        modifier = Modifier
            .padding(20.dp)
            .size(60.dp),
        backgroundColor = backGroundColor
    ) {
        Icon(
            if (isScanning) Icons.Filled.Clear else Icons.Filled.Search,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        )
    }
}




@Composable
fun ResultRow(
    onEventHandler: (ScanScreenEvent) -> Unit,
    isScanning: Boolean,
    noOfDevices: Int,
    permissionsGranted: Boolean
) {

    Row(
        modifier = Modifier.wrapContentHeight()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if(permissionsGranted) "Devices Found: $noOfDevices" else "Grant necessary permissions",
            style = MaterialTheme.typography.h5
        )
        ScanButton(onEventHandler = onEventHandler, isScanning = isScanning, permissionsGranted = permissionsGranted)
    }
}


@Composable
fun DeviceCard(
    device: BluetoothDevice,
    onEventHandler: (ScanScreenEvent) -> Unit
) {

    val deviceName = device.name
    val macAddress = device.address

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .height(200.dp)
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .clickable {
                onEventHandler.invoke(ScanScreenEvent.onDeviceSelected(device))
            },
        backgroundColor = MaterialTheme.colors.surface,
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(id = R.drawable.ic_bluetooth),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.DarkGray),
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.secondary)
                    .padding(4.dp)
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = if((deviceName == null)) "N/A" else deviceName,
                style = MaterialTheme.typography.h5.copy(
                    fontSize = 18.sp
                ))
            Text(
                text = macAddress,
                style = MaterialTheme.typography.h6.copy(
                    color = if(MaterialTheme.colors.isLight) Color.DarkGray else Color.LightGray,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun DevicesGrid(
    devices: List<BluetoothDevice>,
    onEventHandler: (ScanScreenEvent) -> Unit
) {
    LazyVerticalGrid(cells = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        content = {
            items(devices.size) { pos ->
                DeviceCard(device = devices[pos], onEventHandler)
            }
        }
    )
}