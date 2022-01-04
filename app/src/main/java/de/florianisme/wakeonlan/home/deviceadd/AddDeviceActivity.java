package de.florianisme.wakeonlan.home.deviceadd;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.Device;

public class AddDeviceActivity extends ModifyDeviceActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_device_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_device_menu_save) {
            if (assertInputsNotEmptyAndValid()) {
                persistMachine();
                finish();
                return true;
            } else {
                Toast.makeText(this, R.string.add_device_error_save_clicked, Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void persistMachine() {
        Device device = new Device();
        device.name = machineNameInput.getText().toString();
        device.macAddress = machineMacInput.getText().toString();
        device.broadcast_address = machineBroadcastInput.getText().toString();
        device.port = getPort();

        databaseInstance.machineDao().insertAll(device);
    }

}
