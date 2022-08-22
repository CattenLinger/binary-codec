package net.catten.codec.binary;

import org.junit.Test;

import static org.junit.Assert.*;

public class Zen128Test {
    @Test
    public void testByteArrayCodec() {
        for(int i =0; i < 1000; i++) {
            final byte[] givenArray = TestHelper.randomBytes();
            final ByteArrayToStringEncoder encoder = Zen128.getByteArrayToStringEncoder();
            final String encodedString = encoder.encode(givenArray);
            final StringToByteArrayDecoder decoder = Zen128.getStringToByteArrayDecoder();
            final byte[] decodedResult = decoder.decode(encodedString);
            assertArrayEquals("Decoded array contents does not equals to given.", givenArray, decodedResult);
        }
    }
}