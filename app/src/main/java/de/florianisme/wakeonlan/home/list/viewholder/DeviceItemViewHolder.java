package de.florianisme.wakeonlan.home.list.viewholder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.home.deviceadd.EditDeviceActivity;
import de.florianisme.wakeonlan.home.list.DeviceClickedCallback;
import de.florianisme.wakeonlan.persistence.entities.Device;
import de.florianisme.wakeonlan.persistence.models.DeviceStatus;
import de.florianisme.wakeonlan.status.DeviceStatusTester;
import de.florianisme.wakeonlan.status.PingDeviceStatusTester;
import de.florianisme.wakeonlan.wol.WolSender;

public class DeviceItemViewHolder extends RecyclerView.ViewHolder {

    private final View deviceStatus;
    private final TextView deviceName;
    private final TextView deviceMacAddress;

    private final Button editButton;
    private final Button sendWolButton;
    private final DeviceClickedCallback deviceClickedCallback;
    private final DeviceStatusTester deviceStatusTester;

    public DeviceItemViewHolder(View view, DeviceClickedCallback deviceClickedCallback) {
        super(view);
        deviceStatus = view.findViewById(R.id.device_status);
        deviceName = view.findViewById(R.id.machine_name);
        deviceMacAddress = view.findViewById(R.id.machine_mac);

        editButton = view.findViewById(R.id.edit);
        sendWolButton = view.findViewById(R.id.send_wol);
        this.deviceClickedCallback = deviceClickedCallback;
        this.deviceStatusTester = new PingDeviceStatusTester();
    }

    public void setDeviceName(String name) {
        deviceName.setText(name);
    }

    public void setDeviceMacAddress(String mac) {
        deviceMacAddress.setText(mac);
    }

    public void setOnClickHandler(Device device) {
        sendWolButton.setOnClickListener(view -> {
            WolSender.sendWolPacket(device);
            deviceClickedCallback.onDeviceClicked(deviceName.getText().toString());
        });
    }

    public void setOnEditClickHandler(Device device) {
        editButton.setOnClickListener(view -> {
            Context context = view.getContext();

            Intent intent = new Intent(context, EditDeviceActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(EditDeviceActivity.MACHINE_ID_KEY, device.id);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
    }

    public void startDeviceStatusQuery(Device device) {
        setAlphaAnimation();
        deviceStatus.setBackground(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.device_status_unknown));

        deviceStatusTester.scheduleDeviceStatusPings(device, status -> {
            if (status == DeviceStatus.ONLINE) {
                deviceStatus.setBackground(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.device_status_online));
            } else if (status == DeviceStatus.OFFLINE) {
                deviceStatus.setBackground(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.device_status_offline));
            } else {
                deviceStatus.clearAnimation();
                deviceStatus.setBackground(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.device_status_unknown));
            }
        });

    }

    private void setAlphaAnimation() {
        deviceStatus.clearAnimation();
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.4f);
        alphaAnimation.setDuration(1500);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        deviceStatus.setAnimation(alphaAnimation);
    }

    public void cancelStatusUpdates() {
        deviceStatusTester.stopDeviceStatusPings();
    }
}