package my.example;

import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.TinkJsonProtoKeysetFormat;
import com.google.crypto.tink.hybrid.HybridConfig;
import com.google.crypto.tink.hybrid.HybridKeyTemplates;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * Hybrid Encryption Implementation using Google Tink
 * Tink automatically handles RSA + AES-GCM hybrid encryption
 */
public class HybridEncryptionTink {
    
    private static final String PUBLIC_KEYSET_PATH = "keys/tink_public_keyset.json";
    private static final String PRIVATE_KEYSET_PATH = "keys/tink_private_keyset.json";
    
    static {
        try {
            // Register Tink Hybrid encryption
            HybridConfig.register();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to register Tink Hybrid config", e);
        }
    }

    /**
     * Generate and save new key pair for Tink
     * This creates ECIES (Elliptic Curve Integrated Encryption Scheme) keys by default
     */
    public static void generateAndSaveKeys() throws GeneralSecurityException, IOException {
        // Generate private keyset (contains both private and public key)
        KeysetHandle privateKeysetHandle = KeysetHandle.generateNew(
            HybridKeyTemplates.ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM
        );
        
        // Get public keyset from private keyset
        KeysetHandle publicKeysetHandle = privateKeysetHandle.getPublicKeysetHandle();
        
        // Save keysets to files (in JSON format)
        saveKeyset(privateKeysetHandle, PRIVATE_KEYSET_PATH);
        saveKeyset(publicKeysetHandle, PUBLIC_KEYSET_PATH);
        
        System.out.println("Tink keysets generated and saved:");
        System.out.println("  - " + PUBLIC_KEYSET_PATH);
        System.out.println("  - " + PRIVATE_KEYSET_PATH);
    }

    /**
     * Load public keyset for encryption
     */
    public static KeysetHandle loadPublicKeyset(String filePath) throws GeneralSecurityException, IOException {
        String jsonKeyset = new String(Files.readAllBytes(Paths.get(filePath)));
        return TinkJsonProtoKeysetFormat.parseKeyset(jsonKeyset, InsecureSecretKeyAccess.get());
    }

    /**
     * Load private keyset for decryption
     */
    public static KeysetHandle loadPrivateKeyset(String filePath) throws GeneralSecurityException, IOException {
        String jsonKeyset = new String(Files.readAllBytes(Paths.get(filePath)));
        return TinkJsonProtoKeysetFormat.parseKeyset(jsonKeyset, InsecureSecretKeyAccess.get());
    }

    /**
     * Save keyset to file
     */
    private static void saveKeyset(KeysetHandle keysetHandle, String filePath) throws GeneralSecurityException, IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        
        String jsonKeyset = TinkJsonProtoKeysetFormat.serializeKeyset(keysetHandle, InsecureSecretKeyAccess.get());
        Files.write(Paths.get(filePath), jsonKeyset.getBytes());
    }

    /**
     * Encrypt data using Tink hybrid encryption
     */
    public static String encrypt(String plainText, KeysetHandle publicKeysetHandle) throws GeneralSecurityException {
        // Get HybridEncrypt primitive
        HybridEncrypt hybridEncrypt = publicKeysetHandle.getPrimitive(HybridEncrypt.class);
        
        // Encrypt without AAD (contextInfo)
        // Use empty byte array if you don't need additional authenticated data
        byte[] contextInfo = new byte[0];  // No AAD
        byte[] ciphertext = hybridEncrypt.encrypt(plainText.getBytes(), contextInfo);
        
        // Return as Base64 string
        return Base64.getEncoder().encodeToString(ciphertext);
    }

    /**
     * Decrypt data using Tink hybrid encryption
     */
    public static String decrypt(String ciphertextBase64, KeysetHandle privateKeysetHandle) throws GeneralSecurityException {
        // Get HybridDecrypt primitive
        HybridDecrypt hybridDecrypt = privateKeysetHandle.getPrimitive(HybridDecrypt.class);
        
        // Decode from Base64
        byte[] ciphertext = Base64.getDecoder().decode(ciphertextBase64);
        
        // Decrypt without AAD (must match encryption)
        byte[] contextInfo = new byte[0];  // No AAD
        byte[] plaintext = hybridDecrypt.decrypt(ciphertext, contextInfo);
        
        return new String(plaintext);
    }

    /**
     * Check if Tink keysets exist
     */
    public static boolean keysetFilesExist() {
        return new File(PUBLIC_KEYSET_PATH).exists() && 
               new File(PRIVATE_KEYSET_PATH).exists();
    }
}
