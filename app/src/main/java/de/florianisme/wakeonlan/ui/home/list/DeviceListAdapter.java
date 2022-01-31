package de.florianisme.wakeonlan.ui.home.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.entities.Device;
import de.florianisme.wakeonlan.ui.home.list.viewholder.DeviceItemViewHolder;
import de.florianisme.wakeonlan.ui.home.list.viewholder.EmptyViewHolder;
import de.florianisme.wakeonlan.ui.home.list.viewholder.ListViewType;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final DeviceClickedCallback deviceClickedCallback;
    private List<Device> devices = new ArrayList<>();

    public DeviceListAdapter(List<Device> initialDataset, DeviceClickedCallback deviceClickedCallback) {
        updateDevicesList(initialDataset);
        this.deviceClickedCallback = deviceClickedCallback;
    }

    public void updateDataset(List<Device> updatedDevices) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DeviceDiffCallback(updatedDevices, this.devices));
        updateDevicesList(updatedDevices);
        diffResult.dispatchUpdatesTo(this);
    }

    private void updateDevicesList(List<Device> updatedDevices) {
        this.devices = Collections.unmodifiableList(updatedDevices);
    }

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
            viewHolder = new DeviceItemViewHolder(view, deviceClickedCallback);
        }

        return viewHolder;
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
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof DeviceItemViewHolder) {
            ((DeviceItemViewHolder) holder).cancelStatusUpdates();
        }
    }

    @Override
    public long getItemId(int position) {
        if (devices.isEmpty()) {
            return RecyclerView.NO_ID;
        }
        return devices.get(position).id;
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
            DeviceItemViewHolder deviceItemViewHolder = (DeviceItemViewHolder) viewHolder;
            deviceItemViewHolder.cancelStatusUpdates();
            Device device = devices.get(position);

            deviceItemViewHolder.setDeviceName(device.name);
            deviceItemViewHolder.setDeviceMacAddress(device.macAddress);
            deviceItemViewHolder.setOnClickHandler(device);
            deviceItemViewHolder.setOnEditClickHandler(device);
            deviceItemViewHolder.startDeviceStatusQuery(device);
        }
    }
}




