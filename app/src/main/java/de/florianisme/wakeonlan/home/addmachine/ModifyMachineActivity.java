package de.florianisme.wakeonlan.home.addmachine;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.databinding.ActivityModifyMachineBinding;
import de.florianisme.wakeonlan.home.addmachine.validator.BroadcastValidator;
import de.florianisme.wakeonlan.home.addmachine.validator.MacValidator;
import de.florianisme.wakeonlan.home.addmachine.validator.NameValidator;
import de.florianisme.wakeonlan.persistence.AppDatabase;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.Machine;

public class ModifyMachineActivity extends AppCompatActivity {

    protected ActivityModifyMachineBinding binding;
    protected AppDatabase databaseInstance;

    protected TextInputEditText machineMacInput;
    protected TextInputEditText machineNameInput;
    protected TextInputEditText machineBroadcastInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityModifyMachineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        machineMacInput = binding.machine.machineMac;
        machineNameInput = binding.machine.machineName;
        machineBroadcastInput = binding.machine.machineBroadcast;

        setSupportActionBar(binding.toolbar);

        databaseInstance = DatabaseInstanceManager.getDatabaseInstance();
        addValidators();
    }

    private void addValidators() {
        machineMacInput.addTextChangedListener(new MacValidator(machineMacInput));
        machineNameInput.addTextChangedListener(new NameValidator(machineNameInput));
        machineBroadcastInput.addTextChangedListener(new BroadcastValidator(machineBroadcastInput));
    }

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

    protected boolean assertInputsNotEmptyAndValid() {
        return machineMacInput.getError() == null && machineMacInput.getText().length() != 0 &&
                machineNameInput.getError() == null && machineNameInput.getText().length() != 0 &&
                machineBroadcastInput.getError() == null && machineBroadcastInput.getText().length() != 0;
    }

    protected void persistMachine() {
        Machine machine = new Machine();
        machine.name = machineNameInput.getText().toString();
        machine.macAddress = machineMacInput.getText().toString();
        machine.broadcast_address = machineBroadcastInput.getText().toString();
        machine.port = getPort();

        databaseInstance.machineDao().insertAll(machine);
    }

    protected int getPort() {
        return binding.machine.portNine.isChecked() ? 9 : 7;
    }
}
