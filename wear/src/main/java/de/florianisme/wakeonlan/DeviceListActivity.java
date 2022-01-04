package de.florianisme.wakeonlan;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.wear.widget.WearableRecyclerView;

import java.util.List;

import de.florianisme.wakeonlan.databinding.ActivityDeviceListBinding;
import de.florianisme.wakeonlan.models.Device;

public class DeviceListActivity extends Activity {

    private TextView mTextView;
    private ActivityDeviceListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDeviceListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WearableRecyclerView deviceList = binding.deviceList;
        deviceList.setCircularScrollingGestureEnabled(true);

        List<Device> devices = loadDevices();
        populateRecyclerView(devices);
    }

    private List<Device> loadDevices() {
        return null;
    }

    private void populateRecyclerView(List<Device> devices) {

    }
}