package my.example;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Hybrid Encryption Implementation (Java Standard API only, without Bouncy Castle)
 * Encrypts AES key with RSA (public key cryptography) and encrypts data with AES (symmetric cryptography)
 */
public class HybridEncryptionStandard {
    
    private static final String RSA_ALGORITHM = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int RSA_KEY_SIZE = 2048;
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    /**
     * Load public key from PEM file (without Bouncy Castle)
     */
    public static PublicKey loadPublicKey(String filePath) throws Exception {
        String pem = readPemFile(filePath);
        String publicKeyPEM = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        
        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * Load private key from PEM file (without Bouncy Castle)
     */
    public static PrivateKey loadPrivateKey(String filePath) throws Exception {
        String pem = readPemFile(filePath);
        String privateKeyPEM = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        
        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * Read PEM file content
     */
    private static String readPemFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    /**
     * Generate AES key
     */
    private static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        return keyGen.generateKey();
    }

    /**
     * Encrypt data using hybrid encryption
     */
    public static EncryptedData encrypt(String plainText, PublicKey publicKey) throws Exception {
        // 1. Generate AES key
        SecretKey aesKey = generateAESKey();
        
        // 2. Encrypt data with AES
        Cipher aesCipher = Cipher.getInstance(AES_ALGORITHM);
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);
        byte[] encryptedData = aesCipher.doFinal(plainText.getBytes("UTF-8"));
        
        // 3. Encrypt AES key with RSA
        Cipher rsaCipher = Cipher.getInstance(RSA_ALGORITHM);
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedAESKey = rsaCipher.doFinal(aesKey.getEncoded());
        
        return new EncryptedData(encryptedData, encryptedAESKey, iv);
    }

    /**
     * Decrypt data using hybrid encryption
     */
    public static String decrypt(EncryptedData encryptedData, PrivateKey privateKey) throws Exception {
        // 1. Decrypt AES key with RSA
        Cipher rsaCipher = Cipher.getInstance(RSA_ALGORITHM);
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedAESKey = rsaCipher.doFinal(encryptedData.getEncryptedAESKey());
        SecretKey aesKey = new SecretKeySpec(decryptedAESKey, "AES");
        
        // 2. Decrypt data with AES
        Cipher aesCipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, encryptedData.getIv());
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);
        byte[] decryptedData = aesCipher.doFinal(encryptedData.getEncryptedData());
        
        return new String(decryptedData, "UTF-8");
    }

    /**
     * Container class for encrypted data
     */
    public static class EncryptedData {
        private final byte[] encryptedData;
        private final byte[] encryptedAESKey;
        private final byte[] iv;

        public EncryptedData(byte[] encryptedData, byte[] encryptedAESKey, byte[] iv) {
            this.encryptedData = encryptedData;
            this.encryptedAESKey = encryptedAESKey;
            this.iv = iv;
        }

        public byte[] getEncryptedData() {
            return encryptedData;
        }

        public byte[] getEncryptedAESKey() {
            return encryptedAESKey;
        }

        public byte[] getIv() {
            return iv;
        }

        public String toBase64String() {
            return Base64.getEncoder().encodeToString(encryptedData) + ":" +
                   Base64.getEncoder().encodeToString(encryptedAESKey) + ":" +
                   Base64.getEncoder().encodeToString(iv);
        }

        public static EncryptedData fromBase64String(String encoded) {
            String[] parts = encoded.split(":");
            return new EncryptedData(
                Base64.getDecoder().decode(parts[0]),
                Base64.getDecoder().decode(parts[1]),
                Base64.getDecoder().decode(parts[2])
            );
        }
    }
}
