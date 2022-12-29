package de.florianisme.wakeonlan.ui.modify;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.entities.Device;

public class AddDeviceActivity extends ModifyDeviceActivity {

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
        device.broadcast_address = getDeviceBroadcastAddressText();
        device.port = getPort();

        databaseInstance.deviceDao().insertAll(device);
    }

    @Override
    protected boolean inputsHaveNotChanged() {
        // There is no persisted device yet, so we check if any of our inputs are edited
        return getDeviceNameInputText().isEmpty() && getDeviceMacInputText().isEmpty()
                && getDeviceBroadcastAddressText().isEmpty() && getDeviceStatusIpText().isEmpty();
    }
}
