package de.florianisme.wakeonlan.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.list.viewholder.EmptyViewHolder;
import de.florianisme.wakeonlan.list.viewholder.ListViewType;
import de.florianisme.wakeonlan.list.viewholder.WearDeviceItemViewHolder;
import de.florianisme.wakeonlan.model.Device;

public class WearDeviceListAdapter extends RecyclerView.Adapter {

    private List<Device> devices = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;

        if (ListViewType.EMPTY.ordinal() == viewType) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.device_list_empty, viewGroup, false);
            viewHolder = new EmptyViewHolder(view);
        } else {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.device_list_item, viewGroup, false);
            viewHolder = new WearDeviceItemViewHolder(view);
        }

        return viewHolder;
    }

    public void updateDataset(List<Device> devices) {
        this.devices = Collections.unmodifiableList(devices);
    }

    @Override
    public int getItemViewType(int position) {
        if (devices.isEmpty()) {
            return ListViewType.EMPTY.ordinal();
        } else {
            return ListViewType.DEVICE.ordinal();
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
        if (getItemViewType(position) == ListViewType.DEVICE.ordinal()) {
            WearDeviceItemViewHolder wearDeviceItemViewHolder = (WearDeviceItemViewHolder) viewHolder;
            Device device = devices.get(position);

            wearDeviceItemViewHolder.setDeviceName(device.name);
            wearDeviceItemViewHolder.setOnClickHandler(device);
        }
    }
}
