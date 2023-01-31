package de.florianisme.wakeonlan.quickaccess;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.controls.Control;
import android.service.controls.DeviceTypes;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.stream.Collectors;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.AppDatabase;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.entities.Device;

@RequiresApi(api = Build.VERSION_CODES.R)
public class StatelessControlService {

    static List<Control> createStatelessControls(Context context) {
        AppDatabase database = DatabaseInstanceManager.getInstance(context);
        return database.deviceDao().getAll().stream()
                .map(device -> mapDeviceToStatelessControl(device, context))
                .collect(Collectors.toList());
    }


    private static Control mapDeviceToStatelessControl(Device device, Context context) {
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, device.id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new Control.StatelessBuilder(String.valueOf(device.id), pendingIntent)
                .setTitle(device.name)
                .setSubtitle(context.getString(R.string.quick_access_device_subtitle))
                .setDeviceType(DeviceTypes.TYPE_GENERIC_START_STOP)
                .build();
    }

}
