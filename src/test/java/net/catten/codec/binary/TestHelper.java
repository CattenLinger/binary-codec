package net.catten.codec.binary;

import java.util.Random;

public final class TestHelper {
    private final static Random random = new Random(System.currentTimeMillis());

    public static byte[] randomBytes() {
        return randomBytes(256);
    }

    public static byte[] randomBytes(final int randomLength) {
        final byte[] bytes = new byte[random.nextInt(randomLength)];
        random.nextBytes(bytes);
        return bytes;
    }

    public static String byteArrayToString(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for(byte b : array) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
