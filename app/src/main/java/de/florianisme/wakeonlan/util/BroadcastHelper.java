package de.florianisme.wakeonlan.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Optional;

public class BroadcastHelper {

    public static Optional<InetAddress> getBroadcastAddress() throws IOException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface singleInterface = networkInterfaces.nextElement();

            String interfaceName = singleInterface.getName();
            if (interfaceName.contains("wlan0") || interfaceName.contains("eth0")) {
                for (InterfaceAddress interfaceAddress : singleInterface.getInterfaceAddresses()) {
                    InetAddress broadcastAddress = interfaceAddress.getBroadcast();
                    if (broadcastAddress != null) {
                        return Optional.of(broadcastAddress);
                    }
                }
            }
        }
        return Optional.empty();
    }
}
