package de.florianisme.wakeonlan;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.NodeClient;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import de.florianisme.wakeonlan.databinding.ActivityDeviceListBinding;
import de.florianisme.wakeonlan.list.CustomScrollingLayoutCallback;
import de.florianisme.wakeonlan.list.WearDeviceListAdapter;
import de.florianisme.wakeonlan.mobile.DeviceQueryException;
import de.florianisme.wakeonlan.mobile.MobileClient;
import de.florianisme.wakeonlan.mobile.OnDataReceivedListener;
import de.florianisme.wakeonlan.models.DeviceDto;

public class DeviceListActivity extends Activity implements DataClient.OnDataChangedListener, OnDataReceivedListener {

    private ActivityDeviceListBinding binding;
    private WearDeviceListAdapter wearDeviceListAdapter;

    private MessageClient messageClient;
    private DataClient dataClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDeviceListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NodeClient nodeClient = Wearable.getNodeClient(this);
        dataClient = Wearable.getDataClient(this);
        messageClient = Wearable.getMessageClient(this);

        WearableRecyclerView deviceList = binding.deviceList;
        deviceList.setEdgeItemsCenteringEnabled(false);
        deviceList.setLayoutManager(new WearableLinearLayoutManager(this, new CustomScrollingLayoutCallback()));
        wearDeviceListAdapter = new WearDeviceListAdapter(device -> MobileClient.sendDeviceClickedMessage(nodeClient, messageClient, device));
        deviceList.setAdapter(wearDeviceListAdapter);
        deviceList.requestFocus();

        dataClient.addListener(this);
        MobileClient.getDevicesList(nodeClient, dataClient, this);
    }

    private void updateRecyclerviewDataset(List<DeviceDto> devices) {
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
                    List<DeviceDto> devices = MobileClient.buildDeviceList(dataMap);
                    onDataReceived(devices);
                } catch (DeviceQueryException e) {
                    onError(e);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataClient.removeListener(this);
    }

    @Override
    public void onDataReceived(List<DeviceDto> devices) {
        updateRecyclerviewDataset(devices);
    }

    @Override
    public void onError(Exception e) {
        Log.e(this.getClass().getName(), "Error while receiving data from mobile", e);
        Toast.makeText(this, R.string.device_list_no_data, Toast.LENGTH_SHORT).show();
    }
}