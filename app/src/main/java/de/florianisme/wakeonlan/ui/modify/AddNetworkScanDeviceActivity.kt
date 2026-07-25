package de.florianisme.wakeonlan.ui.modify

class AddNetworkScanDeviceActivity : AddDeviceActivity() {

    override fun createFormState(): DeviceFormState {
        val extras = intent.extras
        val state = DeviceFormState()
        if (extras != null) {
            state.name = extras.getString(MACHINE_NAME_KEY).orEmpty()
            state.statusIp = extras.getString(MACHINE_IP_KEY).orEmpty()
            fillBroadcastAddress(state)
        }
        return state
    }

    companion object {
        const val MACHINE_IP_KEY: String = "deviceIp"
        const val MACHINE_NAME_KEY: String = "deviceName"
    }
}


