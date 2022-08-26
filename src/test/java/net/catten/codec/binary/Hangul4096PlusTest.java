package net.catten.codec.binary;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class Hangul4096PlusTest {

    @Test
    public void testByteArraySerializer_Long() {
        for (int i = 0; i < 1000; i++) {
            final byte[] givenArray = TestHelper.randomBytes(1024);
            final Hangul4096Plus hangul4096Plus = Hangul4096Plus.getHangul4096Plus();
            final String encodedResult = hangul4096Plus.serialize(givenArray);
            final byte[] decodedResult = hangul4096Plus.deserialize(encodedResult);
            assertArrayEquals(String.format(
                    "Decoded array contents does not equals to given. \nArray A[@%d]:%s\nArray B[@%d]:%s\n",
                    givenArray.length,
                    TestHelper.byteArrayToString(givenArray),
                    decodedResult.length,
                    TestHelper.byteArrayToString(decodedResult)
            ), givenArray, decodedResult);
        }
    }

    @Test
    public void testByteArraySerializer_Short() {
        for (int i = 0; i < 1000; i++) {
            final byte[] givenArray = TestHelper.randomBytes(16);
            final Hangul4096Plus hangul4096Plus = Hangul4096Plus.getHangul4096Plus();
            final String encodedResult = hangul4096Plus.serialize(givenArray);
            final byte[] decodedResult = hangul4096Plus.deserialize(encodedResult);
            assertArrayEquals(String.format(
                    "Decoded array contents does not equals to given. \nArray A[@%d]:%s\nArray B[@%d]:%s\n",
                    givenArray.length,
                    TestHelper.byteArrayToString(givenArray),
                    decodedResult.length,
                    TestHelper.byteArrayToString(decodedResult)
            ), givenArray, decodedResult);
        }
    }

    @Test
    public void TestByteArraySerializer_Manually() {
        final byte[] givenArray = new byte[]{(byte) 0xab, 0x57};
        final Hangul4096Plus hangul4096Plus = Hangul4096Plus.getHangul4096Plus();
        final String encodedResult = hangul4096Plus.serialize(givenArray);
        final byte[] decodedResult = hangul4096Plus.deserialize(encodedResult);
        assertArrayEquals(String.format(
                "Decoded array contents does not equals to given. \nArray A[@%d]:%s\nArray B[@%d]:%s\n",
                givenArray.length,
                TestHelper.byteArrayToString(givenArray),
                decodedResult.length,
                TestHelper.byteArrayToString(decodedResult)
        ), givenArray, decodedResult);
    }
}