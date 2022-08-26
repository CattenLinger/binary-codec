package net.catten.codec.binary;

import org.junit.Test;

import static org.junit.Assert.*;

public class Zen128Test {
    @Test
    public void testStringByteArraySerializer_Long() {
        for(int i =0; i < 1000; i++) {
            final byte[] givenArray = TestHelper.randomBytes(1024);
            final Zen128 zen128 = Zen128.getDefaultZen128();
            final String encodedString = zen128.serialize(givenArray);
            final byte[] decodedResult = zen128.deserialize(encodedString);
            assertArrayEquals("Decoded array contents does not equals to given.", givenArray, decodedResult);
        }
    }
}