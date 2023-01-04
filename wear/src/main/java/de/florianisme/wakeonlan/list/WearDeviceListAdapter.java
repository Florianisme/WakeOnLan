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
import de.florianisme.wakeonlan.list.viewholder.TitleViewHolder;
import de.florianisme.wakeonlan.list.viewholder.WearDeviceItemViewHolder;
import de.florianisme.wakeonlan.models.DeviceDto;

public class WearDeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DeviceDto> devices = new ArrayList<>();
    private final OnDeviceClickedListener onDeviceClickedListener;

    public WearDeviceListAdapter(OnDeviceClickedListener onDeviceClickedListener) {
        this.onDeviceClickedListener = onDeviceClickedListener;
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
        } else if (ListViewType.TITLE.ordinal() == viewType) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.device_list_title_devices, viewGroup, false);
            viewHolder = new TitleViewHolder(view);
        } else {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.device_list_item, viewGroup, false);
            viewHolder = new WearDeviceItemViewHolder(view);
        }

        return viewHolder;
    }

    public void updateDataset(List<DeviceDto> devices) {
        this.devices = Collections.unmodifiableList(devices);
    }

    @Override
    public int getItemViewType(int position) {
        if (devices.isEmpty()) {
            return ListViewType.EMPTY.ordinal();
        } else if (position == 0) {
            return ListViewType.TITLE.ordinal();
        } else {
            return ListViewType.DEVICE.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        if (devices.isEmpty()) {
            return 1; // "Empty" item
        }
        return devices.size() + 1; // Title element is at index 0
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        if (isActualDeviceViewHolder(position)) {
            WearDeviceItemViewHolder wearDeviceItemViewHolder = (WearDeviceItemViewHolder) viewHolder;
            DeviceDto device = devices.get(position - 1);

            wearDeviceItemViewHolder.setDeviceName(device.getName());
            wearDeviceItemViewHolder.setOnClickHandler(device, onDeviceClickedListener);
        }
    }

    private boolean isActualDeviceViewHolder(int position) {
        return position >= 1;
    }
}
