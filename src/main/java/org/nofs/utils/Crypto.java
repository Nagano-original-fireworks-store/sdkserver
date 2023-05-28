package org.nofs.utils;

import org.nofs.Grasscutter;
import org.nofs.utils.FileUtils;
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

/* loaded from: sdkserver.jar:emu/grasscutter/utils/Crypto.class */
public final class Crypto {
    public static byte[] DISPATCH_KEY;
    public static byte[] DISPATCH_SEED;
    public static byte[] ENCRYPT_KEY;
    public static PrivateKey CUR_SIGNING_KEY;
    private static final SecureRandom secureRandom = new SecureRandom();
    public static byte[] ENCRYPT_SEED_BUFFER = new byte[0];
    public static Map<Integer, PublicKey> EncryptionKeys = new HashMap();

    public static void loadKeys() {
        DISPATCH_KEY = FileUtils.readResource("/keys/dispatchKey.bin");
        DISPATCH_SEED = FileUtils.readResource("/keys/dispatchSeed.bin");
        ENCRYPT_KEY = FileUtils.readResource("/keys/secretKey.bin");
        ENCRYPT_SEED_BUFFER = FileUtils.readResource("/keys/secretKeyBuffer.bin");
        try {
            CUR_SIGNING_KEY = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(FileUtils.readResource("/keys/SigningKey.der")));
            Pattern pattern = Pattern.compile("([0-9]*)_Pub\\.der");
            for (Path path : (List) Objects.requireNonNull(FileUtils.getPathsFromResource("/keys/game_keys"))) {
                if (path.toString().endsWith("_Pub.der")) {
                    Matcher m = pattern.matcher(path.getFileName().toString());
                    if (m.matches()) {
                        PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(FileUtils.read(path)));
                        EncryptionKeys.put(Integer.valueOf(m.group(1)), key);
                    }
                }
            }
        } catch (Exception e) {
            Grasscutter.getLogger().error("An error occurred while loading keys.", (Throwable) e);
        }
    }

    public static void xor(byte[] packet, byte[] key) {
        for (int i = 0; i < packet.length; i++) {
            try {
                int i2 = i;
                packet[i2] = (byte) (packet[i2] ^ key[i % key.length]);
            } catch (Exception e) {
                Grasscutter.getLogger().error("Crypto error.", (Throwable) e);
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
