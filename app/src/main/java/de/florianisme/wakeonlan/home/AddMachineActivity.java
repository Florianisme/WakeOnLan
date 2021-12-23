package de.florianisme.wakeonlan.home;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.databinding.ActivityAddMachineBinding;

public class AddMachineActivity extends AppCompatActivity {

    private ActivityAddMachineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddMachineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_machine_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_machine_menu_save:
                // TODO persist
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
