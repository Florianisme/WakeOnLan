package de.florianisme.wakeonlan.ui.home.details;

import android.os.Bundle;

public class AddNetworkScanDeviceActivity extends AddDeviceActivity {

    public static final String MACHINE_IP_KEY = "deviceIp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepopulateInputs();
    }

    private void prepopulateInputs() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String machineIp = extras.getString(MACHINE_IP_KEY, null);

            deviceStatusIpInput.setText(machineIp);
        }
    }
}
