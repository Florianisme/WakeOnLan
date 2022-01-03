package de.florianisme.wakeonlan.home.list.viewholder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.home.deviceadd.EditDeviceActivity;
import de.florianisme.wakeonlan.persistence.Device;
import de.florianisme.wakeonlan.wol.WolSender;

public class DeviceItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView machineName;
    private final TextView machineMac;
    private final Button editButton;
    private final Button sendWolButton;

    public DeviceItemViewHolder(View view) {
        super(view);
        machineName = view.findViewById(R.id.machine_name);
        machineMac = view.findViewById(R.id.machine_mac);

        editButton = view.findViewById(R.id.edit);
        sendWolButton = view.findViewById(R.id.send_wol);
    }

    public void setMachineName(String name) {
        machineName.setText(name);
    }

    public void setMachineMac(String mac) {
        machineMac.setText(mac);
    }

    public void setOnClickHandler(Device device) {
        sendWolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WolSender.sendWolPacket(device);
                Toast.makeText(view.getContext(), view.getContext().getString(R.string.wol_toast_sending_packet) + machineName.getText(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setOnEditClickHandler(Device device) {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();

                Intent intent = new Intent(context, EditDeviceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(EditDeviceActivity.MACHINE_ID_KEY, device.id);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }
}