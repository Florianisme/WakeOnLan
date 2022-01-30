package de.florianisme.wakeonlan.home.deviceadd;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.entities.Device;

public class EditDeviceActivity extends ModifyDeviceActivity {

    public static final String MACHINE_ID_KEY = "machineId";
    private Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateInputs();
    }

    private void populateInputs() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int machineId = extras.getInt(MACHINE_ID_KEY, -1);
            if (machineId == -1) {
                Toast.makeText(this, R.string.edit_machine_error_loading, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            device = databaseInstance.deviceDao().getById(machineId);

            deviceNameInput.setText(device.name);
            deviceStatusIpInput.setText(device.statusIp);
            deviceMacInput.setText(device.macAddress);
            deviceBroadcastInput.setText(device.broadcast_address);
            if (device.port == 9) {
                devicePorts.setText("9", false);
            } else {
                devicePorts.setText("7", false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_device_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit_machine_menu_save) {
            checkAndPersistDevice();
            return true;
        } else if (item.getItemId() == R.id.edit_machine_menu_delete) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.edit_device_delete_title)
                    .setMessage(R.string.edit_device_delete_message)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        databaseInstance.deviceDao().delete(device);
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    })
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean inputsHaveNotChanged() {
        return device.name.equals(getDeviceNameInputText()) &&
                device.broadcast_address.equals(getDeviceBroadcastAddressText()) &&
                device.macAddress.equals(getDeviceMacInputText()) &&
                device.port == getPort();
    }

    @Override
    protected void persistDevice() {
        device.name = getDeviceNameInputText();
        device.statusIp = getDeviceStatusIpText();
        device.macAddress = getDeviceMacInputText();
        device.broadcast_address = getDeviceBroadcastAddressText();
        device.port = getPort();

        databaseInstance.deviceDao().update(device);
    }

}
