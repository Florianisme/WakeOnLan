package de.florianisme.wakeonlan.ui.home.scan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.ui.home.scan.model.NetworkScanDevice;
import de.florianisme.wakeonlan.ui.home.scan.viewholder.EmptyViewHolder;
import de.florianisme.wakeonlan.ui.home.scan.viewholder.ListViewType;
import de.florianisme.wakeonlan.ui.home.scan.viewholder.ScanResultViewHolder;

public class NetworkScanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final AsyncListDiffer<NetworkScanDevice> listDiffer = new AsyncListDiffer<>(this, new NetworkScanDiffCallback());

    public void clearDataset() {
        listDiffer.submitList(new ArrayList<>());
    }

    public void updateList(List<NetworkScanDevice> updatedList) {
        List<NetworkScanDevice> sortedList = updatedList.stream()
                .distinct()
                .sorted(getScanDeviceComparator())
                .collect(Collectors.toList());

        listDiffer.submitList(sortedList);
    }

    private Comparator<NetworkScanDevice> getScanDeviceComparator() {
        Comparator<NetworkScanDevice> networkScanDeviceComparator =
                Comparator.comparing(device ->
                        Strings.nullToEmpty(device.getIpAddress())
                                .substring(0, device.getIpAddress()
                                        .lastIndexOf(".") + 1));
        return networkScanDeviceComparator.reversed();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;

        if (ListViewType.EMPTY.ordinal() == viewType) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.network_list_empty, parent, false);
            viewHolder = new EmptyViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.network_list_item, parent, false);
            viewHolder = new ScanResultViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (listDiffer.getCurrentList().isEmpty()) {
            return ListViewType.EMPTY.ordinal();
        } else {
            return ListViewType.SCAN_DEVICE.ordinal();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == ListViewType.SCAN_DEVICE.ordinal()) {
            ScanResultViewHolder scanResultViewHolder = (ScanResultViewHolder) viewHolder;

            NetworkScanDevice networkScanDevice = listDiffer.getCurrentList().get(position);

            scanResultViewHolder.setNameIfPresent(networkScanDevice.getName());
            scanResultViewHolder.setIpAddress(networkScanDevice.getIpAddress());
            scanResultViewHolder.setOnAddClickListener(networkScanDevice);
        }
    }

    @Override
    public long getItemId(int position) {
        if (listDiffer.getCurrentList().isEmpty()) {
            return RecyclerView.NO_ID;
        }
        return listDiffer.getCurrentList().get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        if (listDiffer.getCurrentList().isEmpty()) {
            return 1; // "Empty" item
        }
        return listDiffer.getCurrentList().size();
    }
}
