package com.juicy.picturetransfer;

public class Flags {
    public static boolean isFlagSet(byte value, byte flags) {
        return (flags & value) == value;
    }

    public static byte setFlag(byte value, byte flags) {
        return (byte) (flags | value);
    }

    public static byte unsetFlag(byte value, byte flags) {
        return (byte) (flags & ~value);
    }
}
