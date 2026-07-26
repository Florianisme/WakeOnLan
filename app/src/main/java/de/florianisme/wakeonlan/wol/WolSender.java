package de.florianisme.wakeonlan.wol;

import android.util.Log;

import com.google.common.base.Strings;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.ui.modify.BroadcastHelper;

public class WolSender {

    public static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    public static void sendWolPacket(Device device) {
        Runnable sendWolRunnable = new Runnable() {

            @Override
            public void run() {
                // Send the magic packet to all possible broadcast addresses except the one specifically set in the device
                new BroadcastHelper().getAllPossibleBroadcastAddresses().stream()
                        .map(InetAddress::getHostAddress)
                        .filter(address -> !Objects.equals(address, device.broadcastAddress))
                        .forEach(this::sendPacket);
                sendPacket(device.broadcastAddress);
            }

            private void sendPacket(String broadcastAddress) {
                if (Strings.isNullOrEmpty(broadcastAddress)) {
                    return;
                }

                try (DatagramSocket socket = new DatagramSocket()) {
                    DatagramPacket packet = PacketBuilder.buildMagicPacket(broadcastAddress, device.macAddress, device.port, device.secureOnPassword);

                    socket.send(packet);
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), "Error while sending magic packet: ", e);
                }
            }
        };

        EXECUTOR.execute(sendWolRunnable);
    }

}
