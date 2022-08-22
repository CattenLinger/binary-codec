package net.catten.codec.binary;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public final class Hexagram64 {
    private final static char zero = '䷀';
    private final static char nil = '⚋';

    /*
     * String to ByteArray Decoder
     */

    public static StringToByteArrayDecoder getStringToByteArrayDecoder() {
        return decoder;
    }

    private static final Decoder decoder = new Decoder();

    public static class Decoder implements StringToByteArrayDecoder {

        @Override
        public byte[] decode(String str) {
            if(str.length() == 0) return new byte[0];
            int count = 0;
            int length = 0;
            int data = 0;

            ByteArrayOutputStream output = new ByteArrayOutputStream(str.length() * 3 / 4);
            int writePos = 0;

            for(char c : str.toCharArray()) {
                int bits = -1;
                if(c != nil) {
                    int code = c - zero;
                    if(!(code >= 0 && code <= 64)) continue;
                    bits = code;
                }

                if(bits > -1) length++;
                // coerceAtLeast(0)
                if(bits < 0) bits = 0;
                data = (data << 6) | (bits & 0x3F);

                if(++count > 3) {
                    count = 0;
                    while(length-- > 1) {
                        output.write((byte)(((data & 0x00FF0000) >> 16) & 0xFF));
                        writePos++;
                        data = (data << 8) & 0xFFFFFF;
                    }
                }
            }

            return Arrays.copyOf(output.toByteArray(), writePos);
        }
    }

    /*
     * ByteArray to String Encoder
     */

    public static Encoder getByteArrayToStringEncoder() {
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

                    group = group | (bytes[pos++] & 0xFF);
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

}
