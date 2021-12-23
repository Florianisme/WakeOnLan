package de.florianisme.wakeonlan.home.addmachine;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.databinding.ActivityAddMachineBinding;
import de.florianisme.wakeonlan.persistence.AppDatabase;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.Machine;

public class AddMachineActivity extends AppCompatActivity {

    private ActivityAddMachineBinding binding;
    private AppDatabase databaseInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddMachineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        databaseInstance = DatabaseInstanceManager.getDatabaseInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_machine_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_machine_menu_save) {
            persistMachine();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void persistMachine() {
        Machine machine = new Machine();
        machine.name = binding.machine.machineName.getText().toString();
        machine.macAddress = binding.machine.machineMac.getText().toString();
        machine.broadcast_address = binding.machine.machineBroadcast.getText().toString();
        machine.port = getPort();

        databaseInstance.userDao().insertAll(machine);
    }

    private int getPort() {
        return binding.machine.portNine.isChecked() ? 9 : 7;
    }
}
