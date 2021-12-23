package de.florianisme.wakeonlan.home.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.home.list.viewholder.EmptyViewHolder;
import de.florianisme.wakeonlan.home.list.viewholder.ListViewType;
import de.florianisme.wakeonlan.home.list.viewholder.MachineItemViewHolder;
import de.florianisme.wakeonlan.persistence.Machine;

public class MachineListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Machine> machines;

    public MachineListAdapter(List<Machine> machines) {
        this.machines = machines;
    }

    public void updateDataset(List<Machine> machines) {
        this.machines = Collections.unmodifiableList(machines);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;

        if (ListViewType.EMPTY.ordinal() == viewType) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.machine_list_empty, viewGroup, false);
            viewHolder = new EmptyViewHolder(view);
        } else {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.machine_list_item, viewGroup, false);
            viewHolder = new MachineItemViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (machines.isEmpty()) {
            return ListViewType.EMPTY.ordinal();
        } else {
            return ListViewType.MACHINE.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        if (machines.isEmpty()) {
            return 1; // "Empty" item
        }
        return machines.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) == ListViewType.MACHINE.ordinal()) {
            MachineItemViewHolder machineItemViewHolder = (MachineItemViewHolder) viewHolder;
            Machine machine = machines.get(position);

            machineItemViewHolder.setMachineName(machine.name);
            machineItemViewHolder.setMachineMac(machine.macAddress);
            machineItemViewHolder.setOnClickHandler(machine);
            machineItemViewHolder.setOnEditClickHandler(machine);
        }
    }
}




