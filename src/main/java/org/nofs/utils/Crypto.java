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
import java.util.regex.Pattern;

/* loaded from: org.nofs.jar:emu/org.nofs/utils/Crypto.class */
public final class Crypto {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static byte[] DISPATCH_KEY;
    public static byte[] DISPATCH_SEED;

    public static byte[] ENCRYPT_KEY;
    public static long ENCRYPT_SEED = Long.parseUnsignedLong("11468049314633205968");
    public static byte[] ENCRYPT_SEED_BUFFER = new byte[0];

    public static PrivateKey CUR_SIGNING_KEY;

    public static Map<Integer, PublicKey> EncryptionKeys = new HashMap<>();

    public static void loadKeys() {
        DISPATCH_KEY = readResourceFromJar("/keys/dispatchKey.bin");
        DISPATCH_SEED = readResourceFromJar("/keys/dispatchSeed.bin");
        ENCRYPT_KEY = readResourceFromJar("/keys/secretKey.bin");
        ENCRYPT_SEED_BUFFER = readResourceFromJar("/keys/secretKeyBuffer.bin");

        try {
            CUR_SIGNING_KEY = KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(readResourceFromJar("/keys/SigningKey.der")));

            Pattern pattern = Pattern.compile("([0-9]*)_Pub\\.der");
            List<String> keyFiles = FileUtils.getPathsFromResource("/keys/game_keys");
            if (keyFiles != null) {
                for (String path : keyFiles) {
                    if (path.endsWith("_Pub.der")) {
                        String fileName = getFileNameFromPath(path);
                        var m = pattern.matcher(fileName);
                        if (m.matches()) {
                            var key = KeyFactory.getInstance("RSA")
                                    .generatePublic(new X509EncodedKeySpec(readResourceFromJar(path)));
                            EncryptionKeys.put(Integer.valueOf(m.group(1)), key);
                        }
                    }
                }
            }
        } catch (Exception e) {
            org.nofs.sdkserver.getLogger().error("An error occurred while loading keys.", (Throwable) e);
        }
    }

    private static String getFileNameFromPath(String path) {
        int lastSeparatorIndex = path.lastIndexOf('/');
        if (lastSeparatorIndex != -1 && lastSeparatorIndex < path.length() - 1) {
            return path.substring(lastSeparatorIndex + 1);
        }
        return path;
    }

    private static byte[] readResourceFromJar(String resourcePath) {
        try (InputStream inputStream = Crypto.class.getResourceAsStream(resourcePath)) {
            if (inputStream != null) {
                return inputStream.readAllBytes();
            }
        } catch (IOException e) {
            org.nofs.sdkserver.getLogger().error("Failed to read resource: " + resourcePath, e);
        }
        return null;
    }




//    private static List<String> getPathsFromJar(String folderPath) throws IOException {
//        List<String> paths = new ArrayList<>();
//        try (InputStream inputStream = Crypto.class.getResourceAsStream(folderPath);
//             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                paths.add(line);
//            }
//        }
//        return paths;
//    }


    public static void xor(byte[] packet, byte[] key) {
        if (key == null){
            org.nofs.sdkserver.getLogger().error("key null error");
            System.exit(-1);
            return;
        }
        try {
            for (int i = 0; i < packet.length; i++) {
                packet[i] ^= key[i % key.length];
            }
        } catch (Exception e) {
            org.nofs.sdkserver.getLogger().error("Crypto error.", e);
        }
    }

    public static byte[] createSessionKey(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }
}
