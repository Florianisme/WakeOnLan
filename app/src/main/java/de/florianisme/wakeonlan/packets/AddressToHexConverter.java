package de.florianisme.wakeonlan.packets;

public class AddressToHexConverter {

    public static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
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

    public static byte[] getIpBytes(String ipString) throws IllegalArgumentException {
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
