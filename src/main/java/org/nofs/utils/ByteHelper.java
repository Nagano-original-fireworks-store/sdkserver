package org.nofs.utils;

/* loaded from: org.nofs.jar:emu/org.nofs/utils/ByteHelper.class */
public class ByteHelper {
    public static byte[] changeBytes(byte[] a) {
        byte[] b = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[(a.length - i) - 1];
        }
        return b;
    }

    public static byte[] longToBytes(long x) {
        byte[] bytes = {(byte) (x >> 56), (byte) (x >> 48), (byte) (x >> 40), (byte) (x >> 32), (byte) (x >> 24), (byte) (x >> 16), (byte) (x >> 8), (byte) x};
        return bytes;
    }
}
