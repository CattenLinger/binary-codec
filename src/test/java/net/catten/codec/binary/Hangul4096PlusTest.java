package net.catten.codec.binary;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class Hangul4096PlusTest {

    @Test
    public void testByteArrayCodec() {
        for (int i = 0; i < 1000; i++) {
            final byte[] givenArray = TestHelper.randomBytes();
            final ByteArrayToStringEncoder encoder = Hangul4096Plus.getByteArrayToStringEncoder();
            final String encodedResult = encoder.encode(givenArray);
            final StringToByteArrayDecoder decoder = Hangul4096Plus.getStringToByteArrayDecoder();
            final byte[] decodedResult = decoder.decode(encodedResult);

            assertArrayEquals("Decoded array contents does not equals to given.", givenArray, decodedResult);
        }
    }
}