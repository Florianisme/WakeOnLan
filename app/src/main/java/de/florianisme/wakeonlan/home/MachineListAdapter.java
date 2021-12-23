package de.florianisme.wakeonlan.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.Machine;

public class MachineListAdapter extends RecyclerView.Adapter<MachineListAdapter.ViewHolder> {

    private final List<Machine> machines;

    public MachineListAdapter(List<Machine> machines) {
        this.machines = machines;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.machine_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return machines.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Machine machine = machines.get(position);
        viewHolder.setMachineName(machine.name);
        viewHolder.setMachineMac(machine.macAddress);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView machineName;
        private final TextView machineMac;

        public ViewHolder(View view) {
            super(view);
            machineName = view.findViewById(R.id.machine_name);
            machineMac = view.findViewById(R.id.machine_mac);

            Button sendWolButton = view.findViewById(R.id.send_wol);
            sendWolButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(view.getContext(), "Clicked " + machineName.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void setMachineName(String name) {
            machineName.setText(name);
        }

        public void setMachineMac(String mac) {
            machineMac.setText(mac);
        }
    }

}




