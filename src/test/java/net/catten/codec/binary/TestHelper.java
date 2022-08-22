package net.catten.codec.binary;

import java.util.Random;

public final class TestHelper {
    private final static int randomLengthBound = 256;
    private final static Random random = new Random(System.currentTimeMillis());

    public static byte[] randomBytes() {
        final byte[] bytes = new byte[random.nextInt(randomLengthBound)];
        random.nextBytes(bytes);
        return bytes;
    }
}
