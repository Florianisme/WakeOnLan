package de.florianisme.wakeonlan.ui.home.scan;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.common.base.Strings;

import de.florianisme.wakeonlan.ui.home.scan.model.NetworkScanDevice;

public class NetworkScanDiffCallback extends DiffUtil.ItemCallback<NetworkScanDevice> {

    @Override
    public boolean areItemsTheSame(@NonNull NetworkScanDevice oldItem, @NonNull NetworkScanDevice newItem) {
        return Strings.nullToEmpty(oldItem.getMacAddress()).equals(newItem.getMacAddress());
    }

    @Override
    public boolean areContentsTheSame(@NonNull NetworkScanDevice oldItem, @NonNull NetworkScanDevice newItem) {
        return Strings.nullToEmpty(oldItem.getMacAddress()).equals(newItem.getMacAddress()) &&
                Strings.nullToEmpty(oldItem.getIpAddress()).equals(newItem.getIpAddress());
    }
}
