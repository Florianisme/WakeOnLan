package de.florianisme.wakeonlan.shortcuts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.graphics.drawable.IconCompat;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.models.Device;

public class DeviceShortcutFactory {

    private final Device device;

    public DeviceShortcutFactory(Device device) {
        this.device = device;
    }

    public ShortcutInfoCompat buildShortcut(Context context) {
        Intent wakeDeviceIntent = new Intent(context, WakeDeviceActivity.class);
        wakeDeviceIntent.setAction(Intent.ACTION_VIEW);
        Bundle bundle = new Bundle();
        bundle.putInt(WakeDeviceActivity.DEVICE_ID_KEY, device.id);
        wakeDeviceIntent.putExtras(bundle);

        return new ShortcutInfoCompat.Builder(context, String.valueOf(device.id))
                .setShortLabel(device.name)
                .setIntent(wakeDeviceIntent)
                .setIcon(IconCompat.createWithResource(context, R.drawable.device_shortcut))
                .build();
    }
}

