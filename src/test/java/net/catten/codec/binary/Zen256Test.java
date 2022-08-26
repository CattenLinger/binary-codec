package net.catten.codec.binary;

import org.junit.Test;

import static org.junit.Assert.*;

public class Zen256Test {
    @Test
    public void testByteArrayStringCodec_Long() {
        for(int i = 0; i < 1000; i++) {
            final byte[] givenArray = TestHelper.randomBytes(1024);
            final Zen256 zen256 = Zen256.getDefaultZen256();
            final String encodedString = zen256.serialize(givenArray);
            final byte[] decodedResult = zen256.deserialize(encodedString);
            assertArrayEquals("Decoded array contents does not equals to given.", givenArray, decodedResult);
        }
    }
}