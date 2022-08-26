package net.catten.codec.binary;

import java.io.ByteArrayOutputStream;
import java.util.Random;

/**
 * <b>Hangul4096Plus</b> use two korean characters to encode 3 bytes of data.
 * <p>
 * Normal data will be encoded base on the char b+4097 <small>(b means the code of '가',
 * b+4097 means started from the code of '가' + 4097)</small> in 4096 chars
 * <small>(b+4097+4095 = b+8192)</small>.
 * The reason why start from b+4097 but not b+0 is preventing the conflict with the origin
 * Hangul4096 <small>(Author @neruthes)</small>
 * <p>
 * Start from b+8449<small>(b+8193+256+32)</small> is the null character area. Any character
 * greater than or equals the null base will consider be encode terminator.
 * <p>
 * Because the encoding unit size <small>(2 char)</small> is smaller than 3<small>(bytes)</small>,
 * extract chars for padding is used. We use b+8193 to b+8448<small>(b+8193+255, padding area 1)</small>
 * and b+8449 to b+8480<small>(b+8449+31, padding area 2)</small> to encode the padding. <small>(So in
 * fact Hangul4096Plus is Hangul4096+256+32+1Plus, Hangul4384Plus)</small>
 * <p>
 *
 * Normally, when we have Full 3 byte there, the encoding will like this:
 * <pre>
 * 10101010 1010|1010 10101010
 * ---------------------------
 * [  char 1   ]|[  char 2   ]
 * </pre>
 * <p>
 *
 * When we just have 1 byte there, it will be looks like this:
 * <pre>
 * 10101010 ----|---- --------
 * [  char 1   ]|[  ignored  ]
 * </pre>
 * In this situation, we shift the char 1 to right by 4, and use the padding area 1 to
 * encode. Any char lay on this padding area is considered only carrying 1 byte then terminate.
 * <p>
 *
 * When we have 2 bytes, it will be looks like:
 * <pre>
 * 10101010 1010|1010 --------
 * [  char 1   ]|[  char 2   ]
 * </pre>
 * In this situation, we shift the char 2 by 8, use the padding area 2 to encode.
 * Any char lay on this padding area is considered only carrying half byte then terminate.
 */
public final class Hangul4096Plus implements BinaryStringSerializer, BinaryStringDeserializer {

    /****************************************************************
     * Constants
     ****************************************************************/
    private static final int BASE = '가' + 4097;
    private static final int PADDING_1_START = BASE + 4096;
    private static final int PADDING_2_START = PADDING_1_START + 256;
    private static final int PADDING_END = PADDING_2_START + 31;
    private static final int NULL_START = PADDING_END + 1;

    private static final int MASK = 0x0FFF;

    private static final Hangul4096Plus hangul4096Plus = new Hangul4096Plus();

    /****************************************************************
     * Static methods
     ****************************************************************/

    public static Hangul4096Plus getHangul4096Plus() {
        return hangul4096Plus;
    }

    /****************************************************************
     * Instance variables
     ****************************************************************/
    private final Random random = new Random(System.currentTimeMillis());

    /****************************************************************
     * Constructors
     ****************************************************************/
    private Hangul4096Plus() {
    }

    /****************************************************************
     * Implements
     ****************************************************************/

    @Override
    public String serialize(byte[] data) {
        if (data.length == 0) return "";
        final int length = data.length;
        // Pre-allocate
        final StringBuilder sb = new StringBuilder((int) Math.ceil(length / 3.0));
        int readPos = 0;
        while (readPos < length) {
            int readBits = 0;
            int bitBuffer = 0;

            while (readBits < 24 && readPos < length) {
                bitBuffer = (bitBuffer << 8) | (data[readPos++] & 0xFF);
                readBits += 8;
            }

            if (readBits >= 24) {
                while (readBits > 0) {
                    readBits -= 12;
                    sb.append((char) (((bitBuffer >>> readBits) & MASK) + BASE));
                }

                continue;
            }

            switch (readBits) {
                case 8:
                    sb.append((char) ((bitBuffer & 0xFF) + PADDING_1_START));
                    break;
                case 16:
                    sb.append((char) (((bitBuffer >>> 4) & MASK) + BASE));
                    sb.append((char) ((bitBuffer & 0xF) + PADDING_2_START));
                    break;
                default:
                    throw new IllegalStateException("This shouldn't happen. Read bits count should in 8, 16 and 24.");
            }
        }

        return sb.toString();
    }

    @Override
    public byte[] deserialize(String sequence) {
        final int length = sequence.length();
        if (length == 0) return new byte[0];

        final ByteArrayOutputStream output = new ByteArrayOutputStream((int) Math.ceil(length / 2.0 * 3));

        for (int readPos = 0; (readPos < length); ) {
            int readBits = 0;
            int bitBuffer = 0;

            while (readBits < 24 && readPos < length) {
                final int code = sequence.charAt(readPos++);

                if (code < BASE) continue;

                if (code >= PADDING_1_START) {
                    if (code >= PADDING_2_START) {
                        if (code >= NULL_START) break;

                        bitBuffer = (bitBuffer << 4) | (((code - PADDING_2_START) & 0xF));
                        readBits += 4;
                        break;
                    }

                    bitBuffer = (code - PADDING_1_START) & 0xFF;
                    readBits += 8;
                    break;
                }

                bitBuffer = (bitBuffer << 12) | ((code - BASE) & MASK);
                readBits += 12;
            }

            while (readBits > 0) {
                readBits -= 8;
                output.write((byte) ((bitBuffer >> (readBits)) & 0xFF));
            }
        }

        return output.toByteArray();
    }
}
