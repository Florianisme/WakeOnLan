package de.florianisme.wakeonlan.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.ui.list.status.pool.StatusTesterPool;
import de.florianisme.wakeonlan.ui.list.viewholder.DeviceItemViewHolder;
import de.florianisme.wakeonlan.ui.list.viewholder.EmptyViewHolder;
import de.florianisme.wakeonlan.ui.list.viewholder.ListViewType;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final AsyncListDiffer<Device> listDiffer = new AsyncListDiffer<>(this, new DeviceDiffCallback());
    private final DeviceClickedCallback deviceClickedCallback;
    private final StatusTesterPool statusTesterPool;

    public DeviceListAdapter(List<Device> initialDataset, DeviceClickedCallback deviceClickedCallback, StatusTesterPool statusTesterPool) {
        this.deviceClickedCallback = deviceClickedCallback;
        this.statusTesterPool = statusTesterPool;
        updateDataset(initialDataset);
    }

    public void updateDataset(List<Device> updatedDevices) {
        listDiffer.submitList(updatedDevices);
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
            viewHolder = new DeviceItemViewHolder(view, deviceClickedCallback, statusTesterPool);
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (listDiffer.getCurrentList().isEmpty()) {
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
        if (listDiffer.getCurrentList().isEmpty()) {
            return RecyclerView.NO_ID;
        }
        return listDiffer.getCurrentList().get(position).id;
    }

    @Override
    public int getItemCount() {
        if (listDiffer.getCurrentList().isEmpty()) {
            return 1; // "Empty" item
        }
        return listDiffer.getCurrentList().size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) == ListViewType.DEVICE.ordinal()) {
            DeviceItemViewHolder deviceItemViewHolder = (DeviceItemViewHolder) viewHolder;

            Device device = listDiffer.getCurrentList().get(position);
            deviceItemViewHolder.fromDevice(device);
        }
    }
}




