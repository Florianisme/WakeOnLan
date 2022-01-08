package de.florianisme.wakeonlan.list.viewholder;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import de.florianisme.wakeonlan.R;
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

    public void setOnClickHandler(Device device) {
        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO send message to handheld
                Toast.makeText(view.getContext(), view.getContext().getString(R.string.sending_wol) + deviceButton.getText(), Toast.LENGTH_LONG).show();
            }
        });
    }

}