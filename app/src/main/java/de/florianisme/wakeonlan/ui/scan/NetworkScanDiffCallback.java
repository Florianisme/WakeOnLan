package de.florianisme.wakeonlan.ui.scan;

import androidx.recyclerview.widget.DiffUtil;

import com.google.common.base.Strings;

import java.util.List;

import de.florianisme.wakeonlan.ui.scan.model.NetworkScanDevice;

public class NetworkScanDiffCallback extends DiffUtil.Callback {

    private final List<NetworkScanDevice> oldDeviceList;
    private final List<NetworkScanDevice> newDeviceList;

    public NetworkScanDiffCallback(List<NetworkScanDevice> oldDeviceList, List<NetworkScanDevice> newDeviceList) {
        this.oldDeviceList = oldDeviceList;
        this.newDeviceList = newDeviceList;
    }

    @Override
    public int getOldListSize() {
        return oldDeviceList.size();
    }

    @Override
    public int getNewListSize() {
        return newDeviceList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        NetworkScanDevice oldItem = oldDeviceList.get(oldItemPosition);
        NetworkScanDevice newItem = newDeviceList.get(newItemPosition);

        return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        NetworkScanDevice oldItem = oldDeviceList.get(oldItemPosition);
        NetworkScanDevice newItem = newDeviceList.get(newItemPosition);

        return Strings.nullToEmpty(oldItem.getIpAddress()).equals(newItem.getIpAddress())
                && (oldItem.getName().isPresent() && newItem.getName().isPresent()
                && oldItem.getName().get().equals(newItem.getName().get()));

    }
}
