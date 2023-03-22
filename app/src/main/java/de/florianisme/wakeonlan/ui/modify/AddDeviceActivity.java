package de.florianisme.wakeonlan.ui.modify;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.models.Device;

public class AddDeviceActivity extends ModifyDeviceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        triggerRemoteShutdownLayoutVisibility(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_device_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_device_menu_save) {
            checkAndPersistDevice();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void persistDevice() {
        Device device = new Device();
        device.name = getDeviceNameInputText();
        device.statusIp = getDeviceStatusIpText();
        device.macAddress = getDeviceMacInputText();
        device.broadcastAddress = getDeviceBroadcastAddressText();
        device.port = getPort();
        device.secureOnPassword = getDeviceSecureOnPassword();
        device.remoteShutdownEnabled = getDeviceRemoteShutdownEnabled();
        device.sshAddress = getDeviceSshAddress();
        device.sshPort = getDeviceSshPort();
        device.sshUsername = getDeviceSshUsername();
        device.sshPassword = getDeviceSshPassword();
        device.sshCommand = getDeviceSshCommand();

        deviceRepository.insertAll(device);
    }

    @Override
    protected boolean inputsHaveNotChanged() {
        // There is no persisted device yet, so we check if any of our inputs are edited
        return getDeviceNameInputText().isEmpty() && getDeviceMacInputText().isEmpty()
                && getDeviceBroadcastAddressText().isEmpty() && getDeviceStatusIpText().isEmpty()
                && getDeviceSecureOnPassword().isEmpty() && !getDeviceRemoteShutdownEnabled() &&
                getDeviceSshAddress().isEmpty() && getDeviceSshPort() == -1 && getDeviceSshUsername().isEmpty() &&
                getDeviceSshPassword().isEmpty() && getDeviceSshCommand().isEmpty();
    }
}
