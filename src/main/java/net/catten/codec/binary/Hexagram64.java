package net.catten.codec.binary;

import java.io.ByteArrayOutputStream;

public final class Hexagram64 implements BinaryStringSerializer, BinaryStringDeserializer {

    /****************************************************************
     * Constants
     ****************************************************************/

    private final static char zero = '䷀';
    private final static char nil = '⚋';

    private final static Hexagram64 hexagram64 = new Hexagram64();

    /****************************************************************
     * Static methods
     ****************************************************************/

    public static Hexagram64 getHexagram64() {
        return hexagram64;
    }

    /****************************************************************
     * Constructors
     ****************************************************************/

    private Hexagram64() {
    }

    /****************************************************************
     * Implementation
     ****************************************************************/

    @Override
    public byte[] deserialize(String string) {
        final int size = string.length();
        if (size == 0) return new byte[0];
        int count = 0;
        int length = 0;
        int data = 0;

        ByteArrayOutputStream output = new ByteArrayOutputStream(size * 3 / 4);

        for (char c : string.toCharArray()) {
            int bits = -1;
            if (c != nil) {
                int code = c - zero;
                if (!(code >= 0 && code <= 64)) continue;
                bits = code;
            }

            if (bits > -1) length++;
            // coerceAtLeast(0)
            if (bits < 0) bits = 0;
            data = (data << 6) | (bits & 0x3F);

            if (++count > 3) {
                count = 0;
                while (length-- > 1) {
                    output.write((byte) (((data & 0x00FF0000) >> 16) & 0xFF));
                    data = (data << 8) & 0xFFFFFF;
                }
            }
        }

        return output.toByteArray();
    }

    @Override
    public String serialize(byte[] data) {
        final int length = data.length;
        if (length == 0) return "";
        StringBuilder sb = new StringBuilder(length * 4 / 3);
        int padding = 0;
        int pos = 0;

        while (pos < length) {
            int group = 0;

            // 3 * 8 = 24
            for (int i = 0; i < 3; i++) {
                group <<= 8;

                if (pos >= length) {
                    padding++;
                    continue;
                }

                group |= (data[pos++] & 0xFF);
            }

            // 24 / 6 = 4
            for (int i = 0; i < 4 - padding; i++) {
                int index = (group >> 18) & 0x3F;
                sb.append((char) (zero + index));
                group = (group << 6) & 0xFFFFFF;
            }
        }

        for (int i = 0; i < padding; i++) sb.append(nil);

        return sb.toString();
    }
}
