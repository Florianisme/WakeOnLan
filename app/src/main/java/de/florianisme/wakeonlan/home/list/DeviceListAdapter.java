package de.florianisme.wakeonlan.home.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.home.list.viewholder.DeviceItemViewHolder;
import de.florianisme.wakeonlan.home.list.viewholder.EmptyViewHolder;
import de.florianisme.wakeonlan.home.list.viewholder.ListViewType;
import de.florianisme.wakeonlan.persistence.Device;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Device> devices;

    public DeviceListAdapter(List<Device> devices) {
        this.devices = devices;
    }

    public void updateDataset(List<Device> devices) {
        this.devices = Collections.unmodifiableList(devices);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;

        if (ListViewType.EMPTY.ordinal() == viewType) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.device_list_empty, viewGroup, false);
            viewHolder = new EmptyViewHolder(view);
        } else {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.device_list_item, viewGroup, false);
            viewHolder = new DeviceItemViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (devices.isEmpty()) {
            return ListViewType.EMPTY.ordinal();
        } else {
            return ListViewType.MACHINE.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        if (devices.isEmpty()) {
            return 1; // "Empty" item
        }
        return devices.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) == ListViewType.MACHINE.ordinal()) {
            DeviceItemViewHolder deviceItemViewHolder = (DeviceItemViewHolder) viewHolder;
            Device device = devices.get(position);

            deviceItemViewHolder.setMachineName(device.name);
            deviceItemViewHolder.setMachineMac(device.macAddress);
            deviceItemViewHolder.setOnClickHandler(device);
            deviceItemViewHolder.setOnEditClickHandler(device);
        }
    }
}




