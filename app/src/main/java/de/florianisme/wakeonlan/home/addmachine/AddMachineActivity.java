package de.florianisme.wakeonlan.home.addmachine;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.Machine;

public class AddMachineActivity extends ModifyMachineActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_machine_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_machine_menu_save) {
            if (assertInputsNotEmptyAndValid()) {
                persistMachine();
                finish();
                return true;
            } else {
                Toast.makeText(this, R.string.add_machine_error_save_clicked, Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);

    }

    protected void persistMachine() {
        Machine machine = new Machine();
        machine.name = machineNameInput.getText().toString();
        machine.macAddress = machineMacInput.getText().toString();
        machine.broadcast_address = machineBroadcastInput.getText().toString();
        machine.port = getPort();

        databaseInstance.machineDao().insertAll(machine);
    }

}
