package net.catten.codec.binary;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public final class Hangul4096Plus {
    private static final int base = '가' + 4098;
    private static final int paddingBase = base + 4096;
    private static final int bitMask = 0x0FFF;

    /*
     * Decorator
     */

    public static Decorator getDecorator() {
        return decorator;
    }

    private static final Decorator decorator = new Decorator();

    public static class Decorator implements Function<String, String> {
        private static final List<String> segments = Arrays.asList(" ", ", ", ". ");

        private Decorator() {

        }

        @Override
        public String apply(String s) {
            return null;
        }
    }

    /*
     * ByteArray Decoder
     */

    public static StringToByteArrayDecoder getStringToByteArrayDecoder() {
        return decoder;
    }

    private final static Decoder decoder = new Decoder();

    public static class Decoder implements StringToByteArrayDecoder {

        @Override
        public byte[] decode(String str) {
            final int length = str.length();
            if (length == 0) return new byte[0];
            final byte[] bytes = new byte[(int) Math.ceil(length / 2.0 * 3)];
            int writePos = 0;
            int readPos = 0;

            while (readPos < length) {
                int counter = 0;
                int buffer = 0;
                while (counter < 2 && readPos < length) {
                    final int code = str.codePointAt(readPos++);
                    if (code < base) continue;

                    if (code < paddingBase) {
                        buffer = (buffer << 12) | ((code - base) & bitMask);
                        counter++;
                        continue;
                    }

                    buffer = (buffer << 12) | ((code - paddingBase) & bitMask);
                    switch (counter) {
                        case 0:
                            // 11111000_0000 0000_00000000
                            bytes[writePos++] = (byte) ((buffer >> 4) & 0xFF);
                            return Arrays.copyOf(bytes, writePos);
                        case 1:
                            // 00000000_0000 1111_00000000
                            bytes[writePos++] = (byte) ((buffer >> 16) & 0xFF);
                            bytes[writePos++] = (byte) ((buffer >> 8) & 0xFF);
                            return Arrays.copyOf(bytes, writePos);
                    }

                }

                for (int i = 0; i < 3; i++) bytes[writePos++] = (byte) ((buffer >> ((2 - i) * 8)) & 0xFF);
            }

            return Arrays.copyOf(bytes, writePos);
        }
    }

    /*
     * ByteArray Encoder
     */

    public static ByteArrayToStringEncoder getByteArrayToStringEncoder() {
        return encoder;
    }

    private static final Encoder encoder = new Encoder();

    public static class Encoder implements ByteArrayToStringEncoder {
        private Encoder() {

        }

        @Override
        public String encode(byte[] bytes) {
            if (bytes.length == 0) return "";
            final int length = bytes.length;
            final StringBuilder sb = new StringBuilder((int) Math.ceil(bytes.length / 3.0));
            int readPos = 0;
            while (readPos < length) {
                int count = 3;
                int buffer = 0;

                while (count > 0 && readPos < length) {
                    buffer = (buffer << 8) | (bytes[readPos++] & 0xFF);
                    count--;
                }

                if (count == 0) {
                    sb.append((char) (((buffer >> 12) & bitMask) + base));
                    sb.append((char) ((buffer & bitMask) + base));
                    continue;
                }

                switch (count) {
                    case 1:
                        //          00000000_0000 1111
                        // 00000000_0000 1111_00000000
                        buffer = (buffer << 8) & 0x00FFFF00;
                        sb.append((char) (((buffer >> 12) & bitMask) + base));
                        sb.append((char) ((buffer & bitMask) + paddingBase));
                        return sb.toString();
                    case 2:
                        // 11111000_0000 0000_00000000
                        buffer = (buffer << 16) & 0x00FF0000;
                        sb.append((char) (((buffer >> 12) & bitMask) + paddingBase));
                        return sb.toString();
                }
            }

            return sb.toString();
        }
    }
}
