package de.florianisme.wakeonlan.list.viewholder;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.list.OnDeviceClickedListener;
import de.florianisme.wakeonlan.models.DeviceDto;

public class WearDeviceItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView deviceButton;

    public WearDeviceItemViewHolder(View view) {
        super(view);
        deviceButton = view.findViewById(R.id.device_name);
    }

    public void setDeviceName(String name) {
        deviceButton.setText(name);
    }

    public void setOnClickHandler(DeviceDto device, OnDeviceClickedListener onDeviceClickedListener) {
        deviceButton.setOnClickListener(view -> {
            onDeviceClickedListener.onDeviceClicked(device);
            Toast.makeText(view.getContext(), view.getContext().getString(R.string.sending_magic_packet, deviceButton.getText()), Toast.LENGTH_SHORT).show();
        });
    }

}