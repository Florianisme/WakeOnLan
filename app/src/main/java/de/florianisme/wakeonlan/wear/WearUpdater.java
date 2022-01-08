package de.florianisme.wakeonlan.wear;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.florianisme.wakeonlan.persistence.Device;

public class WearUpdater implements DataClient.OnDataChangedListener {

    private final String DEVICE_LIST_PATH = "/device_list";
    private DataClient dataClient;

    public WearUpdater init(Context context) {
        Wearable.getDataClient(context).addListener(this);
        dataClient = Wearable.getDataClient(context);

        return this;
    }

    public void onDeviceListUpdated(List<Device> deviceList) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(DEVICE_LIST_PATH);
        putDataMapRequest.getDataMap().putIntegerArrayList("deviceIds", (ArrayList<Integer>) deviceList.stream().map(device -> device.id).collect(Collectors.toList()));
        putDataMapRequest.getDataMap().putStringArrayList("deviceNames", (ArrayList<String>) deviceList.stream().map(device -> device.name).collect(Collectors.toList()));
        PutDataRequest putDataReq = putDataMapRequest.asPutDataRequest();

        dataClient.putDataItem(putDataReq);
    }

    public void destroy() {
        dataClient.removeListener(this);
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {

    }
}
