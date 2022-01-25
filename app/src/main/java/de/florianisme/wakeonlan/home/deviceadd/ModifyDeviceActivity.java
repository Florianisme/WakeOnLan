package de.florianisme.wakeonlan.home.deviceadd;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.databinding.ActivityModifyDeviceBinding;
import de.florianisme.wakeonlan.home.deviceadd.autocomplete.MacAddressAutocomplete;
import de.florianisme.wakeonlan.home.deviceadd.validator.BroadcastValidator;
import de.florianisme.wakeonlan.home.deviceadd.validator.MacValidator;
import de.florianisme.wakeonlan.home.deviceadd.validator.NameValidator;
import de.florianisme.wakeonlan.persistence.AppDatabase;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.util.BroadcastHelper;

public abstract class ModifyDeviceActivity extends AppCompatActivity {

    protected ActivityModifyDeviceBinding binding;
    protected AppDatabase databaseInstance;

    protected TextInputEditText deviceMacInput;
    protected TextInputEditText deviceNameInput;
    protected TextInputEditText deviceBroadcastInput;
    protected MaterialAutoCompleteTextView devicePorts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityModifyDeviceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        devicePorts = binding.device.machinePorts;
        deviceMacInput = binding.device.machineMac;
        deviceNameInput = binding.device.machineName;
        deviceBroadcastInput = binding.device.machineBroadcast;

        setSupportActionBar(binding.toolbar);

        databaseInstance = DatabaseInstanceManager.getDatabaseInstance();
        addValidators();
        addAutofillClickHandler();
        addDevicePortsAdapter();
    }

    private void addAutofillClickHandler() {
        binding.device.broadcastAutofill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Optional<InetAddress> broadcastAddress = BroadcastHelper.getBroadcastAddress();
                    broadcastAddress.ifPresent(inetAddress -> binding.device.machineBroadcast.setText(inetAddress.getHostAddress()));
                } catch (IOException e) {
                    Log.e(this.getClass().getName(), "Can not retrieve Broadcast Address", e);
                }
            }
        });
    }

    private void addValidators() {
        deviceMacInput.addTextChangedListener(new MacValidator(deviceMacInput));
        deviceMacInput.addTextChangedListener(new MacAddressAutocomplete());
        deviceNameInput.addTextChangedListener(new NameValidator(deviceNameInput));
        deviceBroadcastInput.addTextChangedListener(new BroadcastValidator(deviceBroadcastInput));
    }

    protected boolean assertInputsNotEmptyAndValid() {
        return deviceMacInput.getError() == null && deviceMacInput.getText().length() != 0 &&
                deviceNameInput.getError() == null && deviceNameInput.getText().length() != 0 &&
                deviceBroadcastInput.getError() == null && deviceBroadcastInput.getText().length() != 0;
    }

    private void addDevicePortsAdapter() {
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, R.layout.modify_device_port_dropdown,
                getResources().getStringArray(R.array.ports_selection));
        devicePorts.setAdapter(stringArrayAdapter);
        devicePorts.setText("9", false);
    }

    abstract protected void persistDevice();

    protected int getPort() {
        return "7".equals(binding.device.machinePorts.getText().toString()) ? 7 : 9;
    }

    @NonNull
    private String getInputText(TextInputEditText deviceBroadcastInput) {
        return deviceBroadcastInput.getText().toString().trim();
    }

    @NonNull
    protected String getDeviceBroadcastAddressText() {
        return getInputText(deviceBroadcastInput);
    }

    @NonNull
    protected String getDeviceMacInputText() {
        return getInputText(deviceMacInput);
    }

    @NonNull
    protected String getDeviceNameInputText() {
        return getInputText(deviceNameInput);
    }


}
