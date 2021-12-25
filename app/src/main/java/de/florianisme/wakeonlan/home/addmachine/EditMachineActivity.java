package de.florianisme.wakeonlan.home.addmachine;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.Machine;

public class EditMachineActivity extends ModifyMachineActivity {

    public static final String MACHINE_ID_KEY = "machineId";
    private Machine machine;

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

            machine = databaseInstance.machineDao().getById(machineId);

            machineNameInput.setText(machine.name);
            machineMacInput.setText(machine.macAddress);
            machineBroadcastInput.setText(machine.broadcast_address);
            if (machine.port == 9) {
                machinePorts.setText("9", false);
            } else {
                machinePorts.setText("7", false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_machine_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit_machine_menu_save) {
            if (assertInputsNotEmptyAndValid()) {
                persistMachine();
                finish();
                return true;
            } else {
                Toast.makeText(this, R.string.add_machine_error_save_clicked, Toast.LENGTH_LONG).show();
            }
        } else if (item.getItemId() == R.id.edit_machine_menu_delete) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.edit_machine_delete_title)
                    .setMessage(R.string.edit_machine_delete_message)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        databaseInstance.machineDao().delete(machine);
                        dialog.dismiss();
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    })
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void persistMachine() {
        machine.name = machineNameInput.getText().toString();
        machine.macAddress = machineMacInput.getText().toString();
        machine.broadcast_address = machineBroadcastInput.getText().toString();
        machine.port = getPort();

        databaseInstance.machineDao().update(machine);
    }

}
