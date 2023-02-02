package de.florianisme.wakeonlan.wear;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.stream.Collectors;

import de.florianisme.wakeonlan.models.DeviceDto;
import de.florianisme.wakeonlan.persistence.models.Device;

public class WearClient {

    private static final String DEVICE_LIST_PATH = "/device_list";
    private final DataClient dataClient;

    public WearClient(Context context) {
        dataClient = Wearable.getDataClient(context);
    }

    public void onDeviceListUpdated(List<Device> deviceList) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(DEVICE_LIST_PATH);
        putDataMapRequest.getDataMap().putByteArray("devices", buildDevicesListByteArray(deviceList));
        PutDataRequest putDataReq = putDataMapRequest.asPutDataRequest();

        dataClient.putDataItem(putDataReq);
    }

    private byte[] buildDevicesListByteArray(List<Device> devices) {
        try {
            List<DeviceDto> deviceDtos = devices.stream()
                    .map(device -> new DeviceDto(device.id, device.name))
                    .collect(Collectors.toList());
            return new ObjectMapper().writeValueAsBytes(deviceDtos);
        } catch (JsonProcessingException e) {
            Log.e(getClass().getSimpleName(), "Could not transform list of devices to byte array", e);
            return new byte[0];
        }
    }
}
