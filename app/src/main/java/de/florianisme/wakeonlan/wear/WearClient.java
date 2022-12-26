package de.florianisme.wakeonlan.wear;

import android.content.Context;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.florianisme.wakeonlan.persistence.entities.Device;

public class WearClient {

    private static final String DEVICE_LIST_PATH = "/device_list";
    private final DataClient dataClient;

    public WearClient(Context context) {
        dataClient = Wearable.getDataClient(context);
    }

    public void onDeviceListUpdated(List<Device> deviceList) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(DEVICE_LIST_PATH);
        putDataMapRequest.getDataMap().putIntegerArrayList("deviceIds", (ArrayList<Integer>) deviceList.stream().map(device -> device.id).collect(Collectors.toList()));
        putDataMapRequest.getDataMap().putStringArrayList("deviceNames", (ArrayList<String>) deviceList.stream().map(device -> device.name).collect(Collectors.toList()));
        PutDataRequest putDataReq = putDataMapRequest.asPutDataRequest();

        dataClient.putDataItem(putDataReq);
    }
}
