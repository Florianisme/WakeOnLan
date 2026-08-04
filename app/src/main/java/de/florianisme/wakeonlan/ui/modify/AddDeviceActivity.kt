package de.florianisme.wakeonlan.ui.modify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.florianisme.wakeonlan.R
import de.florianisme.wakeonlan.persistence.repository.DeviceRepository
import de.florianisme.wakeonlan.ui.theme.WakeOnLanTheme

open class AddDeviceActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = DeviceRepository.getInstance(this)
        val state = createFormState()

        setContent {
            WakeOnLanTheme {
                ModifyDeviceScreen(
                    titleRes = R.string.title_activity_add_device,
                    state = state,
                    showDelete = false,
                    onPersist = { device -> repository.insertAll(device) },
                    onDelete = {},
                    onFinish = { finish() },
                )
            }
        }
    }

    /** Overridable so [AddNetworkScanDeviceActivity] can pre-populate the form. */
    protected open fun createFormState(): DeviceFormState {
        val state = DeviceFormState()
        fillBroadcastAddress(state)

        return state
    }
}

