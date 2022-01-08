package de.florianisme.wakeonlan;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.NodeClient;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import de.florianisme.wakeonlan.databinding.ActivityDeviceListBinding;
import de.florianisme.wakeonlan.list.CustomScrollingLayoutCallback;
import de.florianisme.wakeonlan.list.WearDeviceListAdapter;
import de.florianisme.wakeonlan.model.Device;
import de.florianisme.wakeonlan.phone.DeviceFetcher;
import de.florianisme.wakeonlan.phone.DeviceQueryException;
import de.florianisme.wakeonlan.phone.OnDataReceivedListener;

public class DeviceListActivity extends Activity implements DataClient.OnDataChangedListener, OnDataReceivedListener {

    private ActivityDeviceListBinding binding;
    private WearDeviceListAdapter wearDeviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDeviceListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WearableRecyclerView deviceList = binding.deviceList;
        deviceList.setEdgeItemsCenteringEnabled(false);
        deviceList.setLayoutManager(new WearableLinearLayoutManager(this, new CustomScrollingLayoutCallback()));
        wearDeviceListAdapter = new WearDeviceListAdapter();
        deviceList.setAdapter(wearDeviceListAdapter);
        deviceList.requestFocus();

        DataClient dataClient = Wearable.getDataClient(this);
        NodeClient nodeClient = Wearable.getNodeClient(this);

        dataClient.addListener(this);
        DeviceFetcher.getDevicesList(nodeClient, dataClient, this);
    }

    private void updateRecyclerviewDataset(List<Device> devices) {
        wearDeviceListAdapter.updateDataset(devices);
        wearDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent dataEvent : dataEventBuffer) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = dataEvent.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                try {
                    List<Device> devices = DeviceFetcher.buildDeviceList(dataMap);
                    onDataReceived(devices);
                } catch (DeviceQueryException e) {
                    onError(e);
                }
            }
        }
    }

    @Override
    public void onDataReceived(List<Device> devices) {
        updateRecyclerviewDataset(devices);
    }

    @Override
    public void onError(Exception e) {

    }
}