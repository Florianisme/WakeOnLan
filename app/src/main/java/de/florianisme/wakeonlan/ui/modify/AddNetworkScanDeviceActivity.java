package de.florianisme.wakeonlan.ui.modify;

import android.os.Bundle;

public class AddNetworkScanDeviceActivity extends AddDeviceActivity {

    public static final String MACHINE_IP_KEY = "deviceIp";
    public static final String MACHINE_NAME_KEY = "deviceName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepopulateInputs();
    }

    private void prepopulateInputs() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String machineName = extras.getString(MACHINE_NAME_KEY, null);
            String machineIp = extras.getString(MACHINE_IP_KEY, null);

            deviceNameInput.setText(machineName);
            deviceStatusIpInput.setText(machineIp);
            broadcastAutofill.callOnClick();
        }
    }
}
