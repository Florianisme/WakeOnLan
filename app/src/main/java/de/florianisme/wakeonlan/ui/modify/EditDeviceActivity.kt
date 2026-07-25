package de.florianisme.wakeonlan.ui.modify

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.IntentCompat
import de.florianisme.wakeonlan.R
import de.florianisme.wakeonlan.persistence.models.Device
import de.florianisme.wakeonlan.persistence.repository.DeviceRepository
import de.florianisme.wakeonlan.ui.theme.WakeOnLanTheme

class EditDeviceActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val device =
            IntentCompat.getParcelableExtra(intent, DEVICE_PARCELABLE_KEY, Device::class.java)
        if (device == null) {
            Toast.makeText(this, R.string.edit_machine_error_loading, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val repository = DeviceRepository.getInstance(this)
        val state = DeviceFormState(device)

        setContent {
            WakeOnLanTheme {
                ModifyDeviceScreen(
                    titleRes = R.string.title_activity_edit_device,
                    state = state,
                    showDelete = true,
                    onPersist = { updated -> repository.update(updated) },
                    onDelete = { repository.delete(device) },
                    onFinish = { finish() },
                )
            }
        }
    }

    companion object {
        const val DEVICE_PARCELABLE_KEY: String = "machine"
    }
}

