package de.florianisme.wakeonlan.ui.home.scan.viewholder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.ui.home.details.AddNetworkScanDeviceActivity;
import de.florianisme.wakeonlan.ui.home.scan.model.NetworkScanDevice;

public class ScanResultViewHolder extends RecyclerView.ViewHolder {

    private final TextView deviceName;
    private final TextView deviceIp;

    private final Button addDevice;

    public ScanResultViewHolder(@NonNull View itemView) {
        super(itemView);

        deviceName = itemView.findViewById(R.id.scan_device_name);
        deviceIp = itemView.findViewById(R.id.scan_device_ip);
        addDevice = itemView.findViewById(R.id.scan_device_add);
    }

    public void setIpAddress(String ipAddress) {
        deviceIp.setText(ipAddress);
    }

    public void setNameIfPresent(Optional<String> name) {
        if (name.isPresent()) {
            deviceName.setVisibility(View.VISIBLE);
            deviceName.setText(name.get());
            deviceIp.setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body2);
        } else {
            deviceName.setVisibility(View.GONE);
            deviceIp.setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Headline6);
        }
    }

    public void setOnAddClickListener(NetworkScanDevice scanDevice) {
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();

                Intent intent = new Intent(context, AddNetworkScanDeviceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(AddNetworkScanDeviceActivity.MACHINE_NAME_KEY, scanDevice.getName().orElse(null));
                bundle.putString(AddNetworkScanDeviceActivity.MACHINE_IP_KEY, scanDevice.getIpAddress());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }
}
