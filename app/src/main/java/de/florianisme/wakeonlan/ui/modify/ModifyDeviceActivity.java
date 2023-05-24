package de.florianisme.wakeonlan.ui.modify;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.databinding.ActivityModifyDeviceBinding;
import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.persistence.repository.DeviceRepository;
import de.florianisme.wakeonlan.shutdown.listener.ShutdownExecutorListener;
import de.florianisme.wakeonlan.shutdown.test.ShutdownCommandTester;
import de.florianisme.wakeonlan.ui.modify.watcher.autocomplete.MacAddressAutocomplete;
import de.florianisme.wakeonlan.ui.modify.watcher.validator.ConditionalInputNotEmptyValidator;
import de.florianisme.wakeonlan.ui.modify.watcher.validator.InputNotEmptyValidator;
import de.florianisme.wakeonlan.ui.modify.watcher.validator.MacValidator;
import de.florianisme.wakeonlan.ui.modify.watcher.validator.SecureOnPasswordValidator;

public abstract class ModifyDeviceActivity extends AppCompatActivity {

    protected ActivityModifyDeviceBinding binding;
    protected DeviceRepository deviceRepository;

    protected TextInputEditText deviceMacInput;
    protected TextInputEditText deviceNameInput;
    protected TextInputEditText deviceStatusIpInput;
    protected TextInputEditText deviceBroadcastInput;
    protected TextInputEditText deviceSecureOnPassword;
    protected ImageButton broadcastAutofill;
    protected MaterialAutoCompleteTextView devicePorts;
    protected ConstraintLayout deviceRemoteShutdownContainer;
    protected SwitchCompat deviceEnableRemoteShutdown;
    protected TextInputEditText deviceSshAddressInput;
    protected TextInputEditText deviceSshPortInput;
    protected TextInputEditText deviceSshUsernameInput;
    protected TextInputEditText deviceSshPasswordInput;
    protected TextInputEditText deviceSshCommandInput;
    protected Button sshTestShutdownButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityModifyDeviceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        devicePorts = binding.device.devicePorts;
        deviceMacInput = binding.device.deviceMac;
        deviceNameInput = binding.device.deviceName;
        deviceStatusIpInput = binding.device.deviceStatusIp;
        deviceBroadcastInput = binding.device.deviceBroadcast;
        deviceSecureOnPassword = binding.device.deviceSecureOnPassword;
        broadcastAutofill = binding.device.broadcastAutofill;

        deviceRemoteShutdownContainer = binding.device.deviceRemoteShutdownContainer;
        deviceEnableRemoteShutdown = binding.device.deviceSwitchRemoteShutdown;
        deviceSshAddressInput = binding.device.deviceShutdownAddress;
        deviceSshPortInput = binding.device.deviceShutdownPort;
        deviceSshUsernameInput = binding.device.deviceShutdownUsername;
        deviceSshPasswordInput = binding.device.deviceShutdownPassword;
        deviceSshCommandInput = binding.device.deviceShutdownCommand;

        sshTestShutdownButton = binding.device.deviceButtonTestShutdown;

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        deviceRepository = DeviceRepository.getInstance(this);
        addValidators();
        addAutofillClickHandler();
        setRemoteDeviceShutdownSwitchListener();
        addDevicePortsAdapter();
        setOnTestSshShutdownListenerClickedListener();
    }

    private void addAutofillClickHandler() {
        broadcastAutofill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Optional<InetAddress> broadcastAddress = BroadcastHelper.getBroadcastAddress();
                    broadcastAddress.ifPresent(inetAddress -> deviceBroadcastInput.setText(inetAddress.getHostAddress()));
                } catch (IOException e) {
                    Log.e(this.getClass().getName(), "Can not retrieve Broadcast Address", e);
                }
            }
        });
    }

    private void setRemoteDeviceShutdownSwitchListener() {
        deviceEnableRemoteShutdown.setOnCheckedChangeListener((buttonView, isChecked) -> triggerRemoteShutdownLayoutVisibility(isChecked));
    }

    protected void triggerRemoteShutdownLayoutVisibility(boolean isEnabled) {
        deviceRemoteShutdownContainer.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
    }

    private void addValidators() {
        deviceMacInput.addTextChangedListener(new MacValidator(deviceMacInput));
        deviceMacInput.addTextChangedListener(new MacAddressAutocomplete());

        deviceNameInput.addTextChangedListener(new InputNotEmptyValidator(deviceNameInput, R.string.add_device_error_name_empty));
        deviceBroadcastInput.addTextChangedListener(new InputNotEmptyValidator(deviceBroadcastInput, R.string.add_device_error_broadcast_empty));
        deviceSecureOnPassword.addTextChangedListener(new SecureOnPasswordValidator(deviceSecureOnPassword));

        List<Supplier<Boolean>> remoteShutdownEnabledSupplier = Collections.singletonList(() -> deviceEnableRemoteShutdown.isChecked());
        List<Supplier<Boolean>> statusIpFallbackAvailable =
                Lists.newArrayList(() -> deviceEnableRemoteShutdown.isChecked(), () -> isEmpty(deviceStatusIpInput));

        deviceSshAddressInput.addTextChangedListener(new ConditionalInputNotEmptyValidator(deviceSshAddressInput,
                R.string.add_device_error_ssh_address_empty, statusIpFallbackAvailable));
        deviceSshUsernameInput.addTextChangedListener(new ConditionalInputNotEmptyValidator(deviceSshUsernameInput,
                R.string.add_device_error_ssh_username_empty, remoteShutdownEnabledSupplier));
        deviceSshPasswordInput.addTextChangedListener(new ConditionalInputNotEmptyValidator(deviceSshPasswordInput,
                R.string.add_device_error_ssh_password_empty, remoteShutdownEnabledSupplier));
        deviceSshCommandInput.addTextChangedListener(new ConditionalInputNotEmptyValidator(deviceSshCommandInput,
                R.string.add_device_error_ssh_command_empty, remoteShutdownEnabledSupplier));
    }

    protected boolean assertInputsNotEmptyAndValid() {
        return deviceMacInput.getError() == null && isNotEmpty(deviceMacInput) &&
                deviceNameInput.getError() == null && isNotEmpty(deviceNameInput) &&
                deviceBroadcastInput.getError() == null && isNotEmpty(deviceBroadcastInput) &&
                deviceStatusIpInput.getError() == null &&
                deviceSecureOnPassword.getError() == null &&
                deviceSshAddressInput.getError() == null &&
                deviceSshUsernameInput.getError() == null &&
                deviceSshPasswordInput.getError() == null &&
                deviceSshCommandInput.getError() == null;
    }

    private boolean isNotEmpty(TextInputEditText inputEditText) {
        return !isEmpty(inputEditText);
    }

    private boolean isEmpty(TextInputEditText inputEditText) {
        return inputEditText.getText() == null || inputEditText.getText().length() == 0;
    }

    private void addDevicePortsAdapter() {
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, R.layout.modify_device_port_dropdown,
                getResources().getStringArray(R.array.ports_selection));
        devicePorts.setAdapter(stringArrayAdapter);
        devicePorts.setText("9", false);
    }

    protected void checkAndPersistDevice() {
        triggerValidators();
        if (assertInputsNotEmptyAndValid()) {
            persistDevice(buildDeviceFromInputs());
            finish();
        } else {
            Toast.makeText(this, R.string.add_device_error_save_clicked, Toast.LENGTH_LONG).show();
        }
    }

    private void triggerValidators() {
        deviceNameInput.setText(deviceNameInput.getText());
        deviceBroadcastInput.setText(deviceBroadcastInput.getText());
        deviceMacInput.setText(deviceMacInput.getText());
        deviceSecureOnPassword.setText(deviceSecureOnPassword.getText());
        deviceSshAddressInput.setText(deviceSshAddressInput.getText());
        deviceSshUsernameInput.setText(deviceSshUsernameInput.getText());
        deviceSshPasswordInput.setText(deviceSshPasswordInput.getText());
        deviceSshCommandInput.setText(deviceSshCommandInput.getText());
    }

    private void setOnTestSshShutdownListenerClickedListener() {
        sshTestShutdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerValidators();
                if (assertInputsNotEmptyAndValid()) {
                    Device device = buildDeviceFromInputs();

                    final AppCompatDialog dialog = new AppCompatDialog(ModifyDeviceActivity.this);
                    dialog.setContentView(R.layout.dialog_test_remote_shutdown);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    new ShutdownCommandTester(new ShutdownExecutorListener() {
                        @Override
                        public void onTargetHostReached() {
                            runOnUiThread(() -> ((TextView) dialog.findViewById(R.id.test_shutdown_destination_reached)).setText("Host reached"));
                        }

                        @Override
                        public void onLoginSuccessful() {
                            runOnUiThread(() -> ((TextView) dialog.findViewById(R.id.test_shutdown_login_successful)).setText("Login Successful"));
                        }

                        @Override
                        public void onSessionStartSuccessful() {
                            runOnUiThread(() -> ((TextView) dialog.findViewById(R.id.test_shutdown_session_created)).setText("Session start Successful"));
                        }

                        @Override
                        public void onCommandExecuteSuccessful() {
                            runOnUiThread(() -> ((TextView) dialog.findViewById(R.id.test_shutdown_command_executed)).setText("Command exec Successful"));
                        }

                        @Override
                        public void onError(Exception exception) {

                        }

                        private void runOnUiThread(Runnable runnable) {
                            ModifyDeviceActivity.this.runOnUiThread(runnable);
                        }

                    }).startShutdownCommandTest(device);

                    dialog.show();
                }
            }
        });
    }

    abstract protected void persistDevice(Device device);

    abstract protected Device buildDeviceFromInputs();

    abstract protected boolean inputsHaveNotChanged();

    protected int getPort() {
        return "7".equals(binding.device.devicePorts.getText().toString()) ? 7 : 9;
    }

    @NonNull
    private String getInputText(TextInputEditText testInput) {
        return testInput.getText().toString().trim();
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

    @NonNull
    protected String getDeviceStatusIpText() {
        return getInputText(deviceStatusIpInput);
    }

    @NonNull
    protected String getDeviceSecureOnPassword() {
        return getInputText(deviceSecureOnPassword);
    }

    protected boolean getDeviceRemoteShutdownEnabled() {
        return deviceEnableRemoteShutdown.isChecked();
    }

    @NonNull
    protected String getDeviceSshAddress() {
        return getInputText(deviceSshAddressInput);
    }

    @NonNull
    protected Integer getDeviceSshPort() {
        try {
            String sshPort = getInputText(deviceSshPortInput);
            if (Strings.nullToEmpty(sshPort).isEmpty()) {
                return -1;
            }
            return Integer.parseInt(sshPort);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @NonNull
    protected String getDeviceSshUsername() {
        return getInputText(deviceSshUsernameInput);
    }

    @NonNull
    protected String getDeviceSshPassword() {
        return getInputText(deviceSshPasswordInput);
    }

    @NonNull
    protected String getDeviceSshCommand() {
        return getInputText(deviceSshCommandInput);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (inputsHaveNotChanged()) {
            finish();
            return false;
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.modify_device_unsaved_changes_title)
                    .setMessage(R.string.modify_device_unsaved_changes_message)
                    .setPositiveButton(R.string.modify_device_unsaved_changes_positive, (dialog, which) -> checkAndPersistDevice())
                    .setNegativeButton(R.string.modify_device_unsaved_changes_negative, (dialog, which) -> finish())
                    .create().show();
        }

        return false;
    }
}
