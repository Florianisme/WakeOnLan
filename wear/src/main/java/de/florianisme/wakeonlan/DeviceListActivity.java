package de.florianisme.wakeonlan;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.NodeClient;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import de.florianisme.wakeonlan.databinding.ActivityDeviceListBinding;

public class DeviceListActivity extends Activity implements DataClient.OnDataChangedListener {

    private ActivityDeviceListBinding binding;
    private WearDeviceListAdapter wearDeviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDeviceListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WearableRecyclerView deviceList = binding.deviceList;
        deviceList.setCircularScrollingGestureEnabled(false);
        deviceList.setLayoutManager(new WearableLinearLayoutManager(this));
        wearDeviceListAdapter = new WearDeviceListAdapter();
        deviceList.setAdapter(wearDeviceListAdapter);

        DataClient dataClient = Wearable.getDataClient(this);
        NodeClient nodeClient = Wearable.getNodeClient(this);

        dataClient.addListener(this);
        DeviceFetcher.getDevicesList(nodeClient, dataClient, new OnDataReceivedListener() {
            @Override
            public void onDataReceived(List<Device> devices) {
                populateRecyclerView(devices);
            }

            @Override
            public void onError(Exception e) {
                // TODO show error
            }
        });
    }


    private void populateRecyclerView(List<Device> devices) {
        wearDeviceListAdapter.updateDataset(devices);
        wearDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent dataEvent : dataEventBuffer) {
            dataEvent.getType();
        }
    }
}