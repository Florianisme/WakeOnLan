package de.florianisme.wakeonlan.wol;

import androidx.annotation.Nullable;

import com.google.common.base.Strings;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

class PacketBuilder {

    static DatagramPacket buildMagicPacket(String broadcastAddress, String macAddress, int port, @Nullable String secureOnPassword) throws UnknownHostException {

        byte[] macBytes = getMacBytes(macAddress);
        byte[] secureOnPasswordBytes = getSecureOnPasswordBytes(secureOnPassword);

        // Packet is 6 times 0xff, 16 times MAC Address of target and 0, 4 or 6 character password
        byte[] bytes = new byte[6 + (16 * macBytes.length) + secureOnPasswordBytes.length];

        // Append 6 times 0xff
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xff;
        }

        // Append MAC address 16 times
        for (int i = 0; i < 16; i++) {
            appendMacAddress(macBytes, bytes, i);
        }

        // Append Password
        System.arraycopy(secureOnPasswordBytes, 0, bytes, bytes.length - secureOnPasswordBytes.length, secureOnPasswordBytes.length);

        InetAddress address = InetAddress.getByName(broadcastAddress);
        return new DatagramPacket(bytes, bytes.length, address, port);
    }

    private static void appendMacAddress(byte[] macBytes, byte[] bytes, int iteration) {
        System.arraycopy(macBytes, 0, bytes, (iteration + 1) * 6, macBytes.length);
    }

    private static byte[] getSecureOnPasswordBytes(String secureOnPassword) {
        byte[] bytes = Strings.nullToEmpty(secureOnPassword).getBytes(StandardCharsets.US_ASCII);
        if (bytes.length == 0) {
            return new byte[0];
        }

        if (passwordIsIpAddress(secureOnPassword)) {
            return getIpBytes(secureOnPassword);
        } else if (passwordIsMacAddress(secureOnPassword)) {
            return getMacBytes(secureOnPassword);
        } else {
            throw new IllegalArgumentException("Invalid SecureOn Password: Has " + bytes.length + " characters");
        }
    }

    private static boolean passwordIsMacAddress(String password) {
        return Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$").matcher(password).matches();
    }

    private static boolean passwordIsIpAddress(String password) {
        return Pattern.compile("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$").matcher(password).matches();
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }

    private static byte[] getIpBytes(String ipString) throws IllegalArgumentException {
        byte[] bytes = new byte[4];
        String[] ipOctets = ipString.split("(\\.|\\-)");
        if (ipOctets.length != 4) {
            throw new IllegalArgumentException("Invalid IP address.");
        }
        try {
            for (int i = 0; i < 4; i++) {
                int ipOctetNumber = Integer.parseInt(ipOctets[i]);
                String hexString = Integer.toHexString(ipOctetNumber);
                bytes[i] = (byte) Integer.parseInt(hexString, 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in IP address.");
        }
        return bytes;
    }

}
