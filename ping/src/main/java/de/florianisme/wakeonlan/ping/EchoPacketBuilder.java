package de.florianisme.wakeonlan.ping;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

// Copied and modified from https://github.com/dburckh/AndroidPing/blob/master/lib/src/main/java/com/spectrum/android/ping/EchoPacketBuilder.java
public class EchoPacketBuilder {

    public static final int MAX_PAYLOAD = 65507;
    public static final byte TYPE_ICMP_V4 = 8;
    public static final byte TYPE_ICMP_V6 = (byte) 128;
    private static final byte CODE = 0;

    private final byte mType;
    private final byte[] mPayload;
    private short mSequenceNumber;
    private short mIdentifier = 0xDBB;
    private boolean mAutoIdentifier = true;

    private static AtomicInteger sSequence = new AtomicInteger(0);

    public EchoPacketBuilder(final byte type, final byte[] payload) {
        mType = type;
        if (payload == null) {
            mPayload = new byte[0];
        } else if (payload.length > MAX_PAYLOAD) {
            throw new IllegalArgumentException("Payload limited to " + MAX_PAYLOAD);
        } else {
            mPayload = payload;
        }
    }

    public ByteBuffer build() {
        if (mAutoIdentifier) {
            mIdentifier = (short) sSequence.getAndIncrement();
        }
        final byte buffer[] = new byte[8 + mPayload.length];
        final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

        byteBuffer.put(mType);
        byteBuffer.put(CODE);
        final int checkPos = byteBuffer.position();
        byteBuffer.position(checkPos + 2);
        byteBuffer.putShort(mIdentifier);
        byteBuffer.putShort(mSequenceNumber);
        byteBuffer.put(mPayload);
        byteBuffer.putShort(checkPos, checksum(buffer, buffer.length));
        byteBuffer.flip();
        return byteBuffer;
    }

    /**
     * RFC 1071 checksum
     *
     * @param data
     * @param end
     * @return
     */
    static short checksum(byte[] data, int end) {
        int sum = 0;
        // High bytes (even indices)
        for (int i = 0; i < end; i += 2) {
            sum += (data[i] & 0xFF) << 8;
            sum = (sum & 0xFFFF) + (sum >> 16);
        }
        // Low bytes (odd indices)
        for (int i = 1; i < end; i += 2) {
            sum += (data[i] & 0xFF);
            sum = (sum & 0xFFFF) + (sum >> 16);
        }
        // Fix any one's-complement errors- sometimes it is necessary to rotate twice.
        sum = (sum & 0xFFFF) + (sum >> 16);
        return (short) (sum ^ 0xFFFF);
    }
}