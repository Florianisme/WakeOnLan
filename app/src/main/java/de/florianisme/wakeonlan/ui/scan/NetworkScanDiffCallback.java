package de.florianisme.wakeonlan.ui.scan;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.common.base.Strings;

import de.florianisme.wakeonlan.ui.scan.model.NetworkScanDevice;

public class NetworkScanDiffCallback extends DiffUtil.ItemCallback<NetworkScanDevice> {

    @Override
    public boolean areItemsTheSame(@NonNull NetworkScanDevice oldItem, @NonNull NetworkScanDevice newItem) {
        return areContentsTheSame(oldItem, newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull NetworkScanDevice oldItem, @NonNull NetworkScanDevice newItem) {
        return Strings.nullToEmpty(oldItem.getIpAddress()).equals(newItem.getIpAddress())
                && (oldItem.getName().isPresent() && newItem.getName().isPresent() && oldItem.getName().get().equals(newItem.getName().get()));
    }
}
