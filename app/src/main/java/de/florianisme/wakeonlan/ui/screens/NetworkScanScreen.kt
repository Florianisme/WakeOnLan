package de.florianisme.wakeonlan.ui.screens

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.florianisme.wakeonlan.R
import de.florianisme.wakeonlan.ui.modify.AddNetworkScanDeviceActivity
import de.florianisme.wakeonlan.ui.scan.NetworkScanTask
import de.florianisme.wakeonlan.ui.scan.callbacks.ScanCallback
import de.florianisme.wakeonlan.ui.scan.model.NetworkScanDevice
import de.florianisme.wakeonlan.ui.theme.WakeOnLanTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkScanScreen() {
    val context = LocalContext.current
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    val devices = remember { mutableStateListOf<NetworkScanDevice>() }
    var refreshing by remember { mutableStateOf(false) }

    fun startScan() {
        refreshing = true
        devices.clear()

        val callback = object : ScanCallback {
            override fun onError(errorStringReference: Int) {
                mainHandler.post {
                    Toast.makeText(context, errorStringReference, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onDeviceFound(ip: String, hostName: String?) {
                val scanDevice = NetworkScanDevice()
                scanDevice.ipAddress = ip
                if (hostName != null && ip != hostName && hostName.isNotEmpty()) {
                    scanDevice.setName(hostName)
                }
                mainHandler.post {
                    if (!devices.contains(scanDevice)) {
                        devices.add(scanDevice)
                    }
                }
            }

            override fun onTaskEnd() {
                mainHandler.post { refreshing = false }
            }
        }

        NetworkScanTask(callback).startScan(context)
    }

    LaunchedEffect(Unit) { startScan() }

    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = { startScan() },
        modifier = Modifier.fillMaxSize(),
    ) {
        if (devices.isEmpty() && !refreshing) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.network_scan_empty_title),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(items = devices, key = { it.ipAddress }) { device ->
                    ScanResultRow(
                        device = device,
                        onAddClicked = {
                            val intent = Intent(context, AddNetworkScanDeviceActivity::class.java)
                            intent.putExtra(
                                AddNetworkScanDeviceActivity.MACHINE_NAME_KEY,
                                device.name.orElse(null)
                            )
                            intent.putExtra(
                                AddNetworkScanDeviceActivity.MACHINE_IP_KEY,
                                device.ipAddress
                            )
                            context.startActivity(intent)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanResultRow(device: NetworkScanDevice, onAddClicked: () -> Unit) {
    val name = device.name.orElse(null)
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 14.dp,
                bottom = 8.dp,
            )
        ) {
            Text(
                text = name ?: device.ipAddress,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
            )
            if (name != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = device.ipAddress,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.offset(x = (-8).dp)) {
                TextButton(onClick = onAddClicked) {
                    Text(stringResource(R.string.network_scan_button_add))
                }
            }
        }
    }
}


private fun sampleDevice(
    name: String,
) = NetworkScanDevice().apply {
    this.ipAddress = name
}

@Preview(name = "Device cards", showBackground = true)
@Composable
private fun DeviceListContentPreview() {
    WakeOnLanTheme {
        Column {
            ScanResultRow(
                device = sampleDevice("192.168.1.1"),
                onAddClicked = {},
            )
            ScanResultRow(
                device = sampleDevice("192.168.1.2"),
                onAddClicked = {},
            )
            ScanResultRow(
                device = sampleDevice("192.168.1.3"),
                onAddClicked = {},
            )
            ScanResultRow(
                device = sampleDevice("192.168.1.4"),
                onAddClicked = {},
            )
        }
    }
}