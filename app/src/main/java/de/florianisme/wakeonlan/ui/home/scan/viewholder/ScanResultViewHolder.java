package de.florianisme.wakeonlan.ui.home.scan.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.ui.home.scan.model.NetworkScanDevice;

public class ScanResultViewHolder extends RecyclerView.ViewHolder {

    private final TextView deviceIp;
    private final TextView deviceMac;

    private final Button addDevice;

    public ScanResultViewHolder(@NonNull View itemView) {
        super(itemView);

        deviceIp = itemView.findViewById(R.id.scan_device_ip);
        deviceMac = itemView.findViewById(R.id.scan_device_mac);
        addDevice = itemView.findViewById(R.id.scan_device_add);
    }

    public void setIpAddress(String ipAddress) {
        deviceIp.setText(ipAddress);
    }

    public void setMacAddress(String macAddress) {
        deviceMac.setText(macAddress);
    }

    public void setOnAddClickListener(NetworkScanDevice scanDevice) {
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO start AddDevice Activity
            }
        });
    }
}
