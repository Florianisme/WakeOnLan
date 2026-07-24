package de.florianisme.wakeonlan.ui.screens

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import de.florianisme.wakeonlan.R
import de.florianisme.wakeonlan.ui.modify.AddNetworkScanDeviceActivity
import de.florianisme.wakeonlan.ui.scan.NetworkScanTask
import de.florianisme.wakeonlan.ui.scan.callbacks.ScanCallback
import de.florianisme.wakeonlan.ui.scan.model.NetworkScanDevice

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
            LazyColumn(modifier = Modifier.fillMaxSize()) {
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val name = device.name.orElse(null)
            if (name != null) {
                Text(text = name, style = MaterialTheme.typography.titleMedium)
                Text(text = device.ipAddress, style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(text = device.ipAddress, style = MaterialTheme.typography.titleMedium)
            }
            OutlinedButton(
                onClick = onAddClicked,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text(stringResource(R.string.network_scan_button_add))
            }
        }
    }
}

