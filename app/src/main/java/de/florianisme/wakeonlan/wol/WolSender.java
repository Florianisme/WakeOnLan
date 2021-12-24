package de.florianisme.wakeonlan.wol;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import de.florianisme.wakeonlan.persistence.Machine;

public class WolSender {

    public static void sendWolPacket(Machine machine) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    DatagramPacket packet = PacketBuilder.buildMagicPacket(machine.broadcast_address, machine.macAddress, machine.port);
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
