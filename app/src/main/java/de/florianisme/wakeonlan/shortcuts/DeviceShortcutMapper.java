package de.florianisme.wakeonlan.shortcuts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.graphics.drawable.IconCompat;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.models.Device;

public class DeviceShortcutMapper {

    public static ShortcutInfoCompat buildShortcut(Device device, Context context) {
        return new ShortcutInfoCompat.Builder(context, String.valueOf(device.id))
                .setShortLabel(device.name)
                .setIntent(buildIntent(device, context))
                .setIcon(IconCompat.createWithResource(context, R.drawable.device_shortcut))
                .build();
    }

    @NonNull
    private static Intent buildIntent(Device device, Context context) {
        Intent wakeDeviceIntent = new Intent(context, WakeDeviceActivity.class);
        wakeDeviceIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        wakeDeviceIntent.setAction(Intent.ACTION_VIEW);

        Bundle bundle = new Bundle();
        bundle.putInt(WakeDeviceActivity.DEVICE_ID_KEY, device.id);
        wakeDeviceIntent.putExtras(bundle);

        return wakeDeviceIntent;
    }
}

