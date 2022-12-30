package de.florianisme.wakeonlan.packets.arp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import de.florianisme.wakeonlan.packets.AddressToHexConverter;

public class ArpPacketBuilder {

    public static DatagramPacket buildArpPacket(String ownMacAddress, String ownIpAddress, String targetIpAddress, String broadcastAddress) throws UnknownHostException {
        byte[] bytes = new byte[2 + 2 + 1 + 1 + 2 + 6 + 4 + 6 + 4];

        // HRD - Hardware Type (Ethernet)
        bytes[0] = 0;
        bytes[1] = 1;

        // PRO - Protocol Type (IPv4)
        int ipv4ProtocolType = 2048;
        bytes[2] = (byte) (ipv4ProtocolType << 8);
        bytes[3] = (byte) ipv4ProtocolType;

        // HLN - Hardware Address Length (MAC Address = 6)
        bytes[4] = 6;

        // PLN - Protocol Address Length (IPv4 Address = 4)
        bytes[5] = 4;

        // OP - Opcode (ARP Request)
        int arpRequestCode = 1;
        bytes[6] = (byte) (arpRequestCode << 8);
        bytes[7] = (byte) arpRequestCode;

        // SHA - Sender Hardware Address
        byte[] ownMacBytes = AddressToHexConverter.getMacBytes(ownMacAddress);
        bytes[8] = ownMacBytes[0];
        bytes[9] = ownMacBytes[1];
        bytes[10] = ownMacBytes[2];
        bytes[11] = ownMacBytes[3];
        bytes[12] = ownMacBytes[4];
        bytes[13] = ownMacBytes[5];

        // SPA - Sender Protocol Address
        byte[] ownIpBytes = AddressToHexConverter.getIpBytes(ownIpAddress);
        bytes[14] = ownIpBytes[0];
        bytes[15] = ownIpBytes[1];
        bytes[16] = ownIpBytes[2];
        bytes[17] = ownIpBytes[3];

        // THA - Target Hardware Address
        byte[] targetMacBytes = AddressToHexConverter.getMacBytes("00:00:00:00:00:00");
        bytes[18] = targetMacBytes[0];
        bytes[19] = targetMacBytes[1];
        bytes[20] = targetMacBytes[2];
        bytes[21] = targetMacBytes[3];
        bytes[22] = targetMacBytes[4];
        bytes[23] = targetMacBytes[5];


        // TPA - Target Protocol Address
        byte[] targetIpBytes = AddressToHexConverter.getIpBytes(targetIpAddress);
        bytes[24] = targetIpBytes[0];
        bytes[25] = targetIpBytes[1];
        bytes[26] = targetIpBytes[2];
        bytes[27] = targetIpBytes[3];


        InetAddress address = InetAddress.getByName(broadcastAddress);
        return new DatagramPacket(bytes, bytes.length, address, 80);
    }

}
