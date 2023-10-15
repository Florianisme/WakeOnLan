package de.florianisme.wakeonlan.mobile;

import android.net.Uri;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeClient;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import de.florianisme.wakeonlan.models.DeviceDto;

public class MobileClient {

    private static final String DEVICE_LIST_PATH = "/device_list";
    private static final String DEVICE_CLICKED_PATH = "/device_clicked";

    public static void getDevicesList(NodeClient nodeClient, DataClient dataClient, OnDataReceivedListener onDataReceivedListener) {
        nodeClient.getConnectedNodes().addOnSuccessListener(nodes -> queryDevices(dataClient, onDataReceivedListener));
    }

    public static void sendDeviceClickedMessage(NodeClient nodeClient, MessageClient messageClient, DeviceDto device) {
        nodeClient.getConnectedNodes().addOnSuccessListener(nodes -> {
            for (Node node : nodes) {
                messageClient.sendMessage(node.getId(), DEVICE_CLICKED_PATH, new byte[]{(byte) device.getId()});
            }
        });
    }

    private static void queryDevices(DataClient dataClient, OnDataReceivedListener onDataReceivedListener) {
        Uri uri = new Uri.Builder()
                .scheme(PutDataRequest.WEAR_URI_SCHEME)
                .path(DEVICE_LIST_PATH)
                .authority("*")
                .build();

        dataClient.getDataItems(uri).addOnSuccessListener(dataItems -> {
            for (DataItem item : dataItems) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                try {
                    List<DeviceDto> devices = buildDeviceList(dataMap);
                    onDataReceivedListener.onDataReceived(devices);
                } catch (DeviceQueryException e) {
                    onDataReceivedListener.onError(e);
                }
            }
            dataItems.release();
        });
    }

    public static List<DeviceDto> buildDeviceList(DataMap dataMap) throws DeviceQueryException {
        byte[] deviceListBytes = dataMap.getByteArray("devices");

        if (deviceListBytes == null) {
            throw new DeviceQueryException("No bytes received");
        }

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(deviceListBytes));
            return Arrays.asList(new Gson().fromJson(inputStreamReader, DeviceDto[].class));
        } catch (JsonParseException e) {
            throw new DeviceQueryException("Devices can not be parsed", e);
        }
    }

}
