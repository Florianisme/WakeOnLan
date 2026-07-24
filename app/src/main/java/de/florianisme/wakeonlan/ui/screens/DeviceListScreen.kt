package de.florianisme.wakeonlan.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import de.florianisme.wakeonlan.R
import de.florianisme.wakeonlan.persistence.models.Device
import de.florianisme.wakeonlan.persistence.models.DeviceStatus
import de.florianisme.wakeonlan.persistence.repository.DeviceRepository
import de.florianisme.wakeonlan.shutdown.ShutdownExecutor
import de.florianisme.wakeonlan.shutdown.ShutdownModelFactory
import de.florianisme.wakeonlan.ui.list.status.pool.PingStatusTesterPool
import de.florianisme.wakeonlan.ui.list.status.pool.StatusTestType
import de.florianisme.wakeonlan.ui.modify.AddDeviceActivity
import de.florianisme.wakeonlan.ui.modify.EditDeviceActivity
import de.florianisme.wakeonlan.wol.WolSender
import kotlinx.coroutines.launch

private val statusTesterPool = PingStatusTesterPool.getInstance()

@Composable
fun DeviceListScreen() {
    val context = LocalContext.current
    val repository = remember { DeviceRepository.getInstance(context) }
    val devices by repository.allAsObservable.observeAsState(emptyList())

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Mirror the old Fragment lifecycle: pause list pings while not visible, resume otherwise.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> statusTesterPool.pauseAllForType(StatusTestType.LIST)
                Lifecycle.Event.ON_RESUME -> statusTesterPool.resumeAll()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                context.startActivity(Intent(context, AddDeviceActivity::class.java))
            }) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        },
    ) { innerPadding ->
        if (devices.isEmpty()) {
            EmptyDeviceList(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 88.dp,
                ),
            ) {
                items(items = devices, key = { it.id }) { device ->
                    DeviceRow(
                        device = device,
                        onWakeClicked = {
                            WolSender.sendWolPacket(device)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(
                                        R.string.wol_toast_sending_packet,
                                        device.name
                                    )
                                )
                            }
                        },
                        onEditClicked = {
                            val intent = Intent(context, EditDeviceActivity::class.java)
                            intent.putExtra(EditDeviceActivity.DEVICE_PARCELABLE_KEY, device)
                            context.startActivity(intent)
                        },
                        onShutdownClicked = {
                            ShutdownExecutor.shutdownDevice(device)
                            Toast.makeText(
                                context,
                                context.getString(
                                    R.string.remote_shutdown_send_command,
                                    device.name
                                ),
                                Toast.LENGTH_LONG,
                            ).show()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceRow(
    device: Device,
    onWakeClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onShutdownClicked: () -> Unit,
) {
    var status by remember(device.id) { mutableStateOf(DeviceStatus.UNKNOWN) }

    DisposableEffect(device.id) {
        statusTesterPool.schedule(device, { newStatus -> status = newStatus }, StatusTestType.LIST)
        onDispose { statusTesterPool.stopSingle(device, StatusTestType.LIST) }
    }

    val shutdownAvailable = remember(device) { ShutdownModelFactory.fromDevice(device).isPresent }

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
                bottom = 8.dp
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusIndicator(status)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = device.name.orEmpty(),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = device.macAddress.orEmpty().uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.offset(x = (-8).dp),
            ) {
                TextButton(onClick = onEditClicked) {
                    Text(stringResource(R.string.device_list_edit))
                }
                TextButton(onClick = onWakeClicked) {
                    Text(stringResource(R.string.device_list_startup))
                }
                if (shutdownAvailable) {
                    TextButton(onClick = onShutdownClicked) {
                        Text(stringResource(R.string.device_list_shutdown))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusIndicator(status: DeviceStatus) {
    val color = when (status) {
        DeviceStatus.ONLINE -> Color(0xFF4CAF50)
        DeviceStatus.OFFLINE -> MaterialTheme.colorScheme.error
        DeviceStatus.UNKNOWN -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val pulsing = status != DeviceStatus.UNKNOWN
    val infiniteTransition = rememberInfiniteTransition(label = "status")
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "statusAlpha",
    )

    Box(
        modifier = Modifier
            .size(12.dp)
            .alpha(if (pulsing) animatedAlpha else 1f)
            .clip(CircleShape)
            .background(color, CircleShape),
    )
}

@Composable
private fun EmptyDeviceList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.computer_illustration),
            contentDescription = stringResource(R.string.device_list_empty_image),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(120.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.device_list_empty_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.device_list_empty_hint),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}





