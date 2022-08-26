package net.catten.codec.binary;

import org.junit.Test;

import static org.junit.Assert.*;

public class Hexagram64Test {
    @Test
    public void testByteArrayStringSerializer_Long() {
        for(int i = 0; i < 1000; i++) {
            final byte[] givenArray = TestHelper.randomBytes(1024);
            final Hexagram64 hexagram64 = Hexagram64.getHexagram64();
            final String encodedString = hexagram64.serialize(givenArray);
            final byte[] decodedResult = hexagram64.deserialize(encodedString);
            assertArrayEquals("Decoded array contents does not equals to given.", givenArray, decodedResult);
        }
    }
}