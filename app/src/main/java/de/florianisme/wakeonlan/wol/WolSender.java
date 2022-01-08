package de.florianisme.wakeonlan.wol;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import de.florianisme.wakeonlan.persistence.Device;

public class WolSender {

    public static void sendWolPacket(Device device) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    DatagramPacket packet = PacketBuilder.buildMagicPacket(device.broadcast_address, device.macAddress, device.port);
                    DatagramSocket socket = new DatagramSocket();
                    socket.send(packet);
                    socket.close();
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), "Error while sending magic packet: ", e);
                }
            }
        });

        thread.start();


    }

}
