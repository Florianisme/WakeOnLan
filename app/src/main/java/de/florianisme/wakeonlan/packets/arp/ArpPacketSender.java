package de.florianisme.wakeonlan.packets.arp;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ArpPacketSender {

    public static final Executor EXECUTOR = Executors.newFixedThreadPool(4);

    public static void sendArpPacket(String targetIpAddress) {
        Runnable sendArpRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    DatagramPacket packet = ArpPacketBuilder.buildArpPacket("", "", "", "192.168.0.255");
                    DatagramSocket socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    socket.send(packet);
                    socket.close();
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), "Error while sending ARP packet: ", e);
                }
            }
        };

        EXECUTOR.execute(sendArpRunnable);
    }

}
