package de.florianisme.wakeonlan.phone;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeClient;
import com.google.android.gms.wearable.PutDataRequest;

import java.util.ArrayList;
import java.util.List;

import de.florianisme.wakeonlan.model.Device;

public class DeviceFetcher {

    private static final String DEVICE_LIST_PATH = "/device_list";

    public static void getDevicesList(NodeClient nodeClient, DataClient dataClient, OnDataReceivedListener onDataReceivedListener) {
        nodeClient.getConnectedNodes().addOnSuccessListener(new OnSuccessListener<List<Node>>() {
            @Override
            public void onSuccess(List<Node> nodes) {
                queryDevices(dataClient, onDataReceivedListener);
            }
        });
    }

    private static void queryDevices(DataClient dataClient, OnDataReceivedListener onDataReceivedListener) {
        Uri uri = new Uri.Builder()
                .scheme(PutDataRequest.WEAR_URI_SCHEME)
                .path(DEVICE_LIST_PATH)
                .authority("*")
                .build();

        dataClient.getDataItems(uri).addOnSuccessListener(new OnSuccessListener<DataItemBuffer>() {
            @Override
            public void onSuccess(DataItemBuffer dataItems) {
                for (DataItem item : dataItems) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    try {
                        List<Device> devices = buildDeviceList(dataMap);
                        onDataReceivedListener.onDataReceived(devices);
                    } catch (DeviceQueryException e) {
                        onDataReceivedListener.onError(e);
                    }
                }
                dataItems.release();
            }
        });
    }

    public static List<Device> buildDeviceList(DataMap dataMap) throws DeviceQueryException {
        List<Device> devices = new ArrayList<>();
        ArrayList<Integer> deviceIds = dataMap.getIntegerArrayList("deviceIds");
        ArrayList<String> deviceNames = dataMap.getStringArrayList("deviceNames");

        if (deviceIds == null || deviceNames == null) {
            throw new DeviceQueryException("deviceIds or deviceNames not existing");
        }

        for (int i = 0; i < deviceIds.size(); i++) {
            Integer deviceId = deviceIds.get(i);
            String deviceName = deviceNames.get(i);

            devices.add(new Device(deviceId, deviceName));
        }

        return devices;
    }

}
