package com.psyduckco.bleh.device

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import com.psyduckco.bleh.R
import androidx.compose.runtime.remember as remember

enum class DeviceScreenState {
    CONNECTING,
    CONNECTED,
    DISCONNECTED
}

@Composable
fun DeviceScreen(
    onEventHandler: (DeviceScreenEvent) -> Unit,
    viewModel: DeviceViewModel) {

    val contentState = viewModel.contentState.observeAsState(DeviceScreenState.CONNECTING)

    DeviceContent(onEventHandler = onEventHandler,currentState = contentState.value, viewModel = viewModel)
}

@Composable
fun DeviceContent(onEventHandler: (DeviceScreenEvent) -> Unit,
                  currentState: DeviceScreenState,
                  viewModel: DeviceViewModel,
        ) {


    when(currentState) {
        DeviceScreenState.CONNECTING -> {
            onEventHandler.invoke(DeviceScreenEvent.onDeviceConnecting)
            LoadingScreen()
        }
        DeviceScreenState.CONNECTED -> {
            onEventHandler.invoke(DeviceScreenEvent.onDeviceConnected)
            ConnectedScreen(viewModel = viewModel)
        }
        DeviceScreenState.DISCONNECTED -> {
            DisconnectedScreen()
            onEventHandler.invoke(DeviceScreenEvent.onDeviceDisconnected)
        }
    }

}

@Composable
fun ConnectedScreen(viewModel: DeviceViewModel) {

    val activity = LocalContext.current as AppCompatActivity

    var services by remember {
        mutableStateOf(ArrayList<BluetoothGattService>())
    }

    viewModel.services.observe(activity) {
        Log.i("TAG","Service changed.")
        services = it
    }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        ServiceList(services = services)
    }
}

@Composable
fun ServiceList(services: List<BluetoothGattService>) {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(5.dp)
    ) {

        items(items = services, itemContent = { item ->
            ServiceCard(item)
        })
    }
}

@Composable
fun ServiceCard(service: BluetoothGattService) {

    var isExpanded by remember {
        mutableStateOf(false)
    }

    Card(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(5.dp)
    ) {

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                    Text(
                        text = "Gatt Service:",
                        style = MaterialTheme.typography.h6.copy(fontSize = 20.sp, )
                    )

                    Divider()

                    Text(
                        text = service.uuid.toString(),
                        style = MaterialTheme.typography.h6.copy(color = Color.LightGray, fontSize = 16.sp, fontWeight = FontWeight.Light)
                    )
                }

                val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180F else 0F)

                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.DarkGray),
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { isExpanded = !isExpanded }
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.secondary)
                        .padding(8.dp)
                        .rotate(rotationAngle)
                )

            }

            if(isExpanded) {

                Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                    Text(
                        text = "Characteristics:",
                        style = MaterialTheme.typography.h6.copy(fontSize = 20.sp, )
                    )

                    Divider()

                    service.characteristics.forEach { characteristic ->

                        Text(
                            text = characteristic.uuid.toString(),
                            style = MaterialTheme.typography.h6.copy(color = Color.LightGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Light
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                    }
                }

            }
        }
    }
}

@Composable
fun DisconnectedScreen() {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Device not connected.")
        }
    }
}

@Preview
@Composable
fun LoadingScreen() {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .width(100.dp)
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = stringResource(R.string.connecting),
                style = MaterialTheme.typography.h5,
                modifier = Modifier.wrapContentSize()
            )
        }
    }
}