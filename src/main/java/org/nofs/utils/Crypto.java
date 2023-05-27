package org.nofs.utils;

import org.nofs.server.http.objects.QueryCurRegionRspJson;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
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
    public static QueryCurRegionRspJson encryptAndSignRegionData(byte[] regionInfo, String key_id) throws Exception {
        if (key_id == null) {
            throw new Exception("Key ID was not set");
        }

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, EncryptionKeys.get(Integer.valueOf(key_id)));

        //Encrypt regionInfo in chunks
        ByteArrayOutputStream encryptedRegionInfoStream = new ByteArrayOutputStream();

        //Thank you so much GH Copilot
        int chunkSize = 256 - 11;
        int regionInfoLength = regionInfo.length;
        int numChunks = (int) Math.ceil(regionInfoLength / (double) chunkSize);

        for (int i = 0; i < numChunks; i++) {
            byte[] chunk = Arrays.copyOfRange(regionInfo, i * chunkSize,
                    Math.min((i + 1) * chunkSize, regionInfoLength));
            byte[] encryptedChunk = cipher.doFinal(chunk);
            encryptedRegionInfoStream.write(encryptedChunk);
        }

        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(CUR_SIGNING_KEY);
        privateSignature.update(regionInfo);

        var rsp = new QueryCurRegionRspJson();

        rsp.content = Utils.base64Encode(encryptedRegionInfoStream.toByteArray());
        rsp.sign = Utils.base64Encode(privateSignature.sign());
        return rsp;
    }
}
