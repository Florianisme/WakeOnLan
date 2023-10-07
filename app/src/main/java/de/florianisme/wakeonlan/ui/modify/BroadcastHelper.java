package de.florianisme.wakeonlan.ui.modify;

import com.google.common.collect.Lists;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BroadcastHelper {

    private static final List<String> INTERFACE_LIST = Lists.newArrayList("wlan", "eth", "tun");

    public final Optional<InetAddress> getBroadcastAddress() {
        return Collections.list(getNetworkInterfaces()).stream()
                .filter(this::isAllowedInterfaceName)
                .map(NetworkInterface::getInterfaceAddresses)
                .flatMap(Collection::stream)
                .map(InterfaceAddress::getBroadcast)
                .filter(Objects::nonNull)
                .findFirst();
    }

    protected Enumeration<NetworkInterface> getNetworkInterfaces() {
        try {
            return NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return Collections.emptyEnumeration();
        }
    }

    private boolean isAllowedInterfaceName(NetworkInterface networkInterface) {
        return INTERFACE_LIST.stream().anyMatch(interfaceName -> networkInterface.getName().startsWith(interfaceName));
    }
}
