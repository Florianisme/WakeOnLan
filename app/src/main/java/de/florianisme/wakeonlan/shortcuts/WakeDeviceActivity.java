package de.florianisme.wakeonlan.shortcuts;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.base.Strings;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.persistence.repository.DeviceRepository;
import de.florianisme.wakeonlan.wol.WolSender;

public class WakeDeviceActivity extends AppCompatActivity {

    public static final String DEVICE_ID_KEY = "deviceId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wakeDevice();
        finish();
    }

    private void wakeDevice() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int machineId = extras.getInt(DEVICE_ID_KEY, -1);
            if (machineId == -1) {
                Toast.makeText(this, R.string.shortcut_wake_device_error, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Device device = DeviceRepository.getInstance(this).getById(machineId);

            if (!Strings.isNullOrEmpty(device.macAddress)) {
                WolSender.sendWolPacket(device);
                Toast.makeText(this, getString(R.string.wol_toast_sending_packet, device.name), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.shortcut_wake_device_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
