package de.florianisme.wakeonlan.ui.list.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.persistence.models.DeviceStatus;
import de.florianisme.wakeonlan.shutdown.ShutdownExecutor;
import de.florianisme.wakeonlan.shutdown.ShutdownModelFactory;
import de.florianisme.wakeonlan.ui.list.DeviceClickedCallback;
import de.florianisme.wakeonlan.ui.list.status.DeviceStatusTester;
import de.florianisme.wakeonlan.ui.list.status.PingDeviceStatusTester;
import de.florianisme.wakeonlan.ui.modify.EditDeviceActivity;
import de.florianisme.wakeonlan.wol.WolSender;

public class DeviceItemViewHolder extends RecyclerView.ViewHolder {

    private final View deviceStatus;
    private final TextView deviceName;
    private final TextView deviceMacAddress;

    private final Button editButton;
    private final Button sendWolButton;
    private final Button shutdownButton;
    private final DeviceClickedCallback deviceClickedCallback;
    private final DeviceStatusTester deviceStatusTester;

    public DeviceItemViewHolder(View view, DeviceClickedCallback deviceClickedCallback) {
        super(view);
        deviceStatus = view.findViewById(R.id.device_status);
        deviceName = view.findViewById(R.id.device_name);
        deviceMacAddress = view.findViewById(R.id.device_mac);

        editButton = view.findViewById(R.id.edit);
        sendWolButton = view.findViewById(R.id.send_wol);
        shutdownButton = view.findViewById(R.id.shutdown);
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

    public void setShutdownVisibilityAndClickHandler(Device device) {
        boolean shutdownConfigurationValid = ShutdownModelFactory.fromDevice(device).isPresent();

        shutdownButton.setVisibility(shutdownConfigurationValid ? View.VISIBLE : View.GONE);

        if (shutdownConfigurationValid) {
            shutdownButton.setOnClickListener(v -> {
                ShutdownExecutor.shutdownDevice(device);
                Toast.makeText(v.getContext(), v.getContext().getString(R.string.remote_shutdown_send_command, device.name), Toast.LENGTH_LONG).show();
            });
        }
    }

    public void startDeviceStatusQuery(Device device) {
        deviceStatus.clearAnimation();
        deviceStatus.setBackground(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.device_status_unknown));

        deviceStatusTester.scheduleDeviceStatusPings(device, status -> {
            if (status == DeviceStatus.ONLINE) {
                setAlphaAnimationIfNotSet();
                setStatusDrawable(R.drawable.device_status_online);
            } else if (status == DeviceStatus.OFFLINE) {
                setAlphaAnimationIfNotSet();
                setStatusDrawable(R.drawable.device_status_offline);
            } else {
                deviceStatus.clearAnimation();
                deviceStatus.setBackground(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.device_status_unknown));
            }
        });
    }

    private void setStatusDrawable(int statusDrawable) {
        Drawable[] drawables = {
                deviceStatus.getBackground(),
                AppCompatResources.getDrawable(itemView.getContext(), statusDrawable)
        };

        TransitionDrawable transitionDrawable = new TransitionDrawable(drawables);
        deviceStatus.setBackground(transitionDrawable);
        transitionDrawable.startTransition(600);
    }

    private void setAlphaAnimationIfNotSet() {
        if (deviceStatus.getAnimation() == null) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.4f);
            alphaAnimation.setDuration(1500);
            alphaAnimation.setRepeatCount(Animation.INFINITE);
            alphaAnimation.setInterpolator(new AccelerateInterpolator());
            alphaAnimation.setRepeatMode(Animation.REVERSE);
            deviceStatus.startAnimation(alphaAnimation);
        }
    }

    public void cancelStatusUpdates() {
        if (deviceStatusTester != null) {
            deviceStatusTester.stopDeviceStatusPings();
        }
    }
}