package de.florianisme.wakeonlan.ui.modify;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.userauth.UserAuthException;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.databinding.ActivityModifyDeviceBinding;
import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.persistence.repository.DeviceRepository;
import de.florianisme.wakeonlan.shutdown.ShutdownModel;
import de.florianisme.wakeonlan.shutdown.exception.CommandExecuteException;
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
        broadcastAutofill.setOnClickListener(v -> {
            Optional<InetAddress> broadcastAddress = new BroadcastHelper().getBroadcastAddress();
            broadcastAddress.ifPresent(inetAddress -> deviceBroadcastInput.setText(inetAddress.getHostAddress()));
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

                    View view = LayoutInflater.from(ModifyDeviceActivity.this).inflate(R.layout.dialog_test_remote_shutdown, null);
                    AlertDialog dialog = new MaterialAlertDialogBuilder(ModifyDeviceActivity.this)
                            .setView(view)
                            .setTitle(R.string.remote_shutdown_send_command_dialog_title)
                            .setPositiveButton(android.R.string.ok, (dlg, which) -> dlg.dismiss())
                            .create();

                    final LinearLayout destinationReachedResult = view.findViewById(R.id.result_destination_reached);
                    final LinearLayout authorizationResult = view.findViewById(R.id.result_authorization);
                    final LinearLayout sessionCreatedResult = view.findViewById(R.id.result_session);
                    final LinearLayout commandExecutedResult = view.findViewById(R.id.result_command_execute);

                    final TextView optionalErrorMessage = view.findViewById(R.id.result_optional_error_message);
                    optionalErrorMessage.setVisibility(View.GONE);

                    setInitialDialogTexts(destinationReachedResult, authorizationResult, sessionCreatedResult, commandExecutedResult);

                    new ShutdownCommandTester(new ShutdownExecutorListener() {
                        @Override
                        public void onTargetHostReached() {
                            setStepSuccessfullyCompleted(destinationReachedResult, R.string.test_shutdown_successful_destination);
                        }

                        @Override
                        public void onLoginSuccessful() {
                            setStepSuccessfullyCompleted(authorizationResult, R.string.test_shutdown_successful_authorization);
                        }

                        @Override
                        public void onSessionStartSuccessful() {
                            setStepSuccessfullyCompleted(sessionCreatedResult, R.string.test_shutdown_successful_session);
                        }

                        @Override
                        public void onCommandExecuteSuccessful() {
                            setStepSuccessfullyCompleted(commandExecutedResult, R.string.test_shutdown_successful_command_execute);
                        }

                        @Override
                        public void onSudoPromptTriggered(ShutdownModel shutdownModel) {
                            runOnUiThread(() -> {
                                optionalErrorMessage.setVisibility(View.VISIBLE);
                                optionalErrorMessage.setText(getString(R.string.test_shutdown_error_execution_sudo_prompt, shutdownModel.getCommand()));
                            });
                        }

                        @Override
                        public void onGeneralError(Exception exception, ShutdownModel shutdownModel) {
                            runOnUiThread(() -> {
                                optionalErrorMessage.setVisibility(View.VISIBLE);
                                optionalErrorMessage.setText(getTextByExceptionType(exception, shutdownModel));
                            });
                        }

                    }).startShutdownCommandTest(device);

                    dialog.show();
                }
            }

            private String getTextByExceptionType(Exception exception, ShutdownModel shutdownModel) {
                if (exception instanceof ConnectException) {
                    return getString(R.string.test_shutdown_error_connect_exception, shutdownModel.getSshAddress(), shutdownModel.getSshPort());
                } else if (exception instanceof UnknownHostException) {
                    return getString(R.string.test_shutdown_error_unknown_host, shutdownModel.getSshAddress());
                } else if (exception instanceof UserAuthException) {
                    return getString(R.string.test_shutdown_error_auth_exception, shutdownModel.getUsername(), shutdownModel.getSshAddress());
                } else if (exception instanceof ConnectionException && Throwables.getRootCause(exception) instanceof TimeoutException) {
                    return getString(R.string.test_shutdown_error_execution_timeout, shutdownModel.getCommand());
                } else if (exception instanceof CommandExecuteException) {
                    Integer exitStatus = ((CommandExecuteException) exception).getExitStatus();
                    String explanationString = getExitCodeExplanationStringRes(exitStatus);
                    return getString(R.string.test_shutdown_error_execution_exception, shutdownModel.getCommand(), exitStatus, explanationString);
                }

                return getString(R.string.test_shutdown_error_unknown_exception, exception.getMessage());
            }

            private String getExitCodeExplanationStringRes(Integer exitStatus) {
                switch (exitStatus) {
                    case 127:
                        return getString(R.string.execution_error_command_not_found);
                    case 126:
                        return getString(R.string.execution_error_command_not_executable);
                    default:
                        return getString(R.string.execution_error_unknown);
                }
            }

            private void runOnUiThread(Runnable runnable) {
                ModifyDeviceActivity.this.runOnUiThread(runnable);
            }

            private void setStepSuccessfullyCompleted(LinearLayout layout, int stringResourceId) {
                runOnUiThread(() -> {
                    TextView resultMessage = getResultMessageView(layout);
                    RadioButton resultIndicator = getResultRadioButton(layout);

                    resultMessage.setText(stringResourceId);
                    resultIndicator.setChecked(true);
                    resultIndicator.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#479c44")));
                });
            }
        });
    }

    private void setInitialDialogTexts(LinearLayout destinationReachedResult, LinearLayout authorizationResult,
                                       LinearLayout sessionCreatedResult, LinearLayout commandExecutedResult) {
        setTexts(getResultMessageView(destinationReachedResult), R.string.test_shutdown_initial_destination);
        setTexts(getResultMessageView(authorizationResult), R.string.test_shutdown_initial_authorization);
        setTexts(getResultMessageView(sessionCreatedResult), R.string.test_shutdown_initial_session);
        setTexts(getResultMessageView(commandExecutedResult), R.string.test_shutdown_initial_command_execute);
    }

    private void setTexts(TextView resultMessageView, int stringResourceId) {
        resultMessageView.setText(stringResourceId);
    }

    private RadioButton getResultRadioButton(LinearLayout layout) {
        return layout.findViewById(R.id.test_shutdown_item_radio);
    }

    private TextView getResultMessageView(LinearLayout layout) {
        return layout.findViewById(R.id.test_shutdown_item_result_message);
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
