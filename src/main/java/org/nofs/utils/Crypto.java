package org.nofs.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: org.nofs.jar:emu/org.nofs/utils/Crypto.class */
public final class Crypto {

    public static byte[] DISPATCH_KEY;
    public static byte[] DISPATCH_SEED;
    public static byte[] ENCRYPT_KEY;
    public static PrivateKey CUR_SIGNING_KEY;
    private static final SecureRandom secureRandom = new SecureRandom();
    public static byte[] ENCRYPT_SEED_BUFFER = new byte[0];
    public static Map<Integer, PublicKey> EncryptionKeys = new HashMap();

    public static void loadKeys() {
        DISPATCH_KEY = readResourceFromJar("/keys/dispatchKey.bin");
        DISPATCH_SEED = readResourceFromJar("/keys/dispatchSeed.bin");
        ENCRYPT_KEY = readResourceFromJar("/keys/secretKey.bin");
        ENCRYPT_SEED_BUFFER = readResourceFromJar("/keys/secretKeyBuffer.bin");
        try {
            CUR_SIGNING_KEY = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(FileUtils.readResource("/keys/SigningKey.der")));
            Pattern pattern = Pattern.compile("([0-9]*)_Pub\\.der");
            for (Object path : (List) Objects.requireNonNull(FileUtils.getPathsFromResource("/keys/game_keys"))) {
                if (path.toString().endsWith("_Pub.der")) {
                    Matcher m = pattern.matcher(path.getClass().toString());
                    if (m.matches()) {
                        PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(readResourceFromJar((String) path)));
                        EncryptionKeys.put(Integer.valueOf(m.group(1)), key);
                    }
                }
            }
        } catch (Exception e) {
            org.nofs.sdkserver.getLogger().error("An error occurred while loading keys.", (Throwable) e);
        }
    }

    private static byte[] readResourceFromJar(String resourcePath) {
        try (InputStream inputStream = Crypto.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream != null) {
                return inputStream.readAllBytes();
            }
        } catch (IOException e) {
            org.nofs.sdkserver.getLogger().error("Failed to read resource: " + resourcePath, e);
        }
        return null;
    }



    public static void xor(byte[] packet, byte[] key) {
        for (int i = 0; i < packet.length; i++) {
            try {
                int i2 = i;
                packet[i2] = (byte) (packet[i2] ^ key[i % key.length]);
            } catch (Exception e) {
                org.nofs.sdkserver.getLogger().error("Crypto error.", (Throwable) e);
                return;
            }
        }
    }

    public static byte[] createSessionKey(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }
}
