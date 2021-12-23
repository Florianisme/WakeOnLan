package de.florianisme.wakeonlan.wol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import de.florianisme.wakeonlan.persistence.Machine;

public class WolSender {

    public static void sendWolPacket(Machine machine) throws Exception {
        DatagramPacket packet = PacketBuilder.buildMagicPacket(machine.broadcast_address, machine.macAddress, machine.port);
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
        socket.close();
    }

}
