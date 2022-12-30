package de.florianisme.wakeonlan.packets.wol;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.florianisme.wakeonlan.persistence.entities.Device;

public class WolSender {

    public static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    public static void sendWolPacket(Device device) {
        Runnable sendWolRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    DatagramPacket packet = PacketBuilder.buildMagicPacket(device.broadcastAddress, device.macAddress, device.port, device.secureOnPassword);
                    DatagramSocket socket = new DatagramSocket();
                    socket.send(packet);
                    socket.close();
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), "Error while sending magic packet: ", e);
                }
            }
        };

        EXECUTOR.execute(sendWolRunnable);
    }

}
