package de.florianisme.wakeonlan.home.addmachine;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import de.florianisme.wakeonlan.databinding.ActivityModifyMachineBinding;
import de.florianisme.wakeonlan.home.addmachine.validator.BroadcastValidator;
import de.florianisme.wakeonlan.home.addmachine.validator.MacValidator;
import de.florianisme.wakeonlan.home.addmachine.validator.NameValidator;
import de.florianisme.wakeonlan.persistence.AppDatabase;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;

public abstract class ModifyMachineActivity extends AppCompatActivity {

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

    protected boolean assertInputsNotEmptyAndValid() {
        return machineMacInput.getError() == null && machineMacInput.getText().length() != 0 &&
                machineNameInput.getError() == null && machineNameInput.getText().length() != 0 &&
                machineBroadcastInput.getError() == null && machineBroadcastInput.getText().length() != 0;
    }

    abstract protected void persistMachine();

    protected int getPort() {
        return binding.machine.portNine.isChecked() ? 9 : 7;
    }
}
