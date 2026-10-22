package de.florianisme.wakeonlan.wol;

import android.util.Log;

import com.google.common.base.Strings;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.stream.Stream;

import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.ui.modify.BroadcastAddressLister;

public class WolRunnable implements Runnable {

    private static final BroadcastAddressLister BROADCAST_ADDRESS_LISTER = new BroadcastAddressLister();

    private final Device device;

    public WolRunnable(Device device) {
        this.device = device;
    }

    @Override
    public void run() {
        // Send the magic packet to all possible broadcast addresses and the one specifically set in the device
        List<InetAddress> allBroadcastAddresses = BROADCAST_ADDRESS_LISTER.getAllPossibleBroadcastAddresses();

        Stream.concat(Stream.of(device.broadcastAddress),
                        allBroadcastAddresses.stream().map(InetAddress::getHostAddress))
                .distinct()
                .forEach(this::sendPacket);
    }

    private void sendPacket(String broadcastAddress) {
        if (Strings.isNullOrEmpty(broadcastAddress)) {
            return;
        }

        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = PacketBuilder.buildMagicPacket(broadcastAddress, device.macAddress, device.port, device.secureOnPassword);

            socket.setBroadcast(true);
            socket.send(packet);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error while sending magic packet: ", e);
        }
    }
}
