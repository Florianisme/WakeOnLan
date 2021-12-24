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
import de.florianisme.wakeonlan.home.addmachine.EditMachineActivity;
import de.florianisme.wakeonlan.persistence.Machine;
import de.florianisme.wakeonlan.wol.WolSender;

public class MachineItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView machineName;
    private final TextView machineMac;
    private final Button editButton;
    private final Button sendWolButton;

    public MachineItemViewHolder(View view) {
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

    public void setOnClickHandler(Machine machine) {
        sendWolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WolSender.sendWolPacket(machine);
                Toast.makeText(view.getContext(), view.getContext().getString(R.string.wol_toast_sending_packet) + machineName.getText(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setOnEditClickHandler(Machine machine) {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();

                Intent intent = new Intent(context, EditMachineActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(EditMachineActivity.MACHINE_ID_KEY, machine.id);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }
}