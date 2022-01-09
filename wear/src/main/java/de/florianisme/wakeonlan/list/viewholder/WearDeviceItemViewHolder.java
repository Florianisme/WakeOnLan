package de.florianisme.wakeonlan.list.viewholder;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.list.OnDeviceClickedListener;
import de.florianisme.wakeonlan.model.Device;

public class WearDeviceItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView deviceButton;

    public WearDeviceItemViewHolder(View view) {
        super(view);
        deviceButton = view.findViewById(R.id.device_name);
    }

    public void setDeviceName(String name) {
        deviceButton.setText(name);
    }

    public void setOnClickHandler(Device device, OnDeviceClickedListener onDeviceClickedListener) {
        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeviceClickedListener.onDeviceClicked(device);
                Toast.makeText(view.getContext(), view.getContext().getString(R.string.sending_magic_packet) + deviceButton.getText(), Toast.LENGTH_LONG).show();
            }
        });
    }

}