package net.catten.codec.binary;

public interface BinaryStringSerializer {
    /**
     * Encode a byte array to string
     */
    String serialize(final byte[] data);
}
