package de.florianisme.wakeonlan.home.list.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.Machine;
import de.florianisme.wakeonlan.wol.WolSender;

public class MachineItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView machineName;
    private final TextView machineMac;
    private final Button sendWolButton;

    public MachineItemViewHolder(View view) {
        super(view);
        machineName = view.findViewById(R.id.machine_name);
        machineMac = view.findViewById(R.id.machine_mac);

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
                Toast.makeText(view.getContext(), "Sending Magic Packet to " + machineName.getText(), Toast.LENGTH_LONG).show();

                sendWolPacket();
            }

            private void sendWolPacket() {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            WolSender.sendWolPacket(machine);
                        } catch (Exception e) {
                            Log.e(this.getClass().getName(), "Error while sending magic packet: ", e);
                        }
                    }
                });

                thread.start();
            }
        });
    }
}