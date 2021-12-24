package de.florianisme.wakeonlan.util;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

public class BroadcastHelper {

    public static Optional<InetAddress> getBroadcastAddress(Context context) throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();

        if (dhcp == null) {
            return Optional.empty();
        }

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) (broadcast >> (k * 8));
        return Optional.of(InetAddress.getByAddress(quads));
    }

}
