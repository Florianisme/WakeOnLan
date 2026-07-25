package de.florianisme.wakeonlan

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import de.florianisme.wakeonlan.mobile.DeviceQueryException
import de.florianisme.wakeonlan.mobile.MobileClient
import de.florianisme.wakeonlan.mobile.OnDataReceivedListener
import de.florianisme.wakeonlan.models.DeviceDto

class DeviceListActivity : ComponentActivity(), DataClient.OnDataChangedListener,
    OnDataReceivedListener {

    private val devices = mutableStateListOf<DeviceDto>()

    private lateinit var nodeClient: NodeClient
    private lateinit var dataClient: DataClient
    private lateinit var messageClient: MessageClient

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        nodeClient = Wearable.getNodeClient(this)
        dataClient = Wearable.getDataClient(this)
        messageClient = Wearable.getMessageClient(this)

        setContent {
            WearDeviceList(
                devices = devices,
                onDeviceClicked = { device ->
                    MobileClient.sendDeviceClickedMessage(nodeClient, messageClient, device)
                    Toast.makeText(
                        this,
                        getString(R.string.sending_magic_packet, device.name),
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )
        }

        dataClient.addListener(this)
        MobileClient.getDevicesList(nodeClient, dataClient, this)
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        for (dataEvent in dataEventBuffer) {
            if (dataEvent.type == DataEvent.TYPE_CHANGED) {
                val dataMap = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                try {
                    onDataReceived(MobileClient.buildDeviceList(dataMap))
                } catch (e: DeviceQueryException) {
                    onError(e)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dataClient.removeListener(this)
    }

    override fun onDataReceived(devices: List<DeviceDto>) {
        this.devices.clear()
        this.devices.addAll(devices)
    }

    override fun onError(e: Exception) {
        Log.e(javaClass.name, "Error while receiving data from mobile", e)
        Toast.makeText(this, R.string.device_list_no_data, Toast.LENGTH_SHORT).show()
    }
}



