package my.example;

import com.google.crypto.tink.KeysetHandle;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Hybrid Encryption Demo
 */
public class JavaMain {

    private static final String PUBLIC_KEY_PATH = "keys/public_key.pem";
    private static final String PRIVATE_KEY_PATH = "keys/private_key.pem";

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Hybrid Encryption Demo");
        System.out.println("========================================\n");

        // Demo 1: With Bouncy Castle
        demoWithBouncyCastle();

        System.out.println("\n========================================\n");

        // Demo 2: Without Bouncy Castle (Java Standard API only)
        demoWithStandardAPI();

        System.out.println("\n========================================\n");

        // Demo 3: With Google Tink
        demoWithGoogleTink();
    }

    private static void demoWithBouncyCastle() {
        try {
            System.out.println("=== Demo 1: Using Bouncy Castle ===\n");

            // 1. Load RSA keys
            System.out.println("1. Loading RSA keys...");
            PublicKey publicKey = HybridEncryption.loadPublicKey(PUBLIC_KEY_PATH);
            PrivateKey privateKey = HybridEncryption.loadPrivateKey(PRIVATE_KEY_PATH);
            System.out.println("   Done\n");

            // 2. Original data to encrypt
            String originalText = "This is confidential information. It will be securely protected by hybrid encryption.";
            System.out.println("2. Original data:");
            System.out.println("   " + originalText + "\n");

            // 3. Encrypt
            System.out.println("3. Encrypting...");
            HybridEncryption.EncryptedData encryptedData =
                    HybridEncryption.encrypt(originalText, publicKey);
            String encoded = encryptedData.toBase64String();
            System.out.println("   Done");
            System.out.println("   Encrypted data (Base64):");
            System.out.println("   " + encoded.substring(0, Math.min(100, encoded.length())) + "...\n");

            // 4. Decrypt
            System.out.println("4. Decrypting...");
            HybridEncryption.EncryptedData dataToDecrypt =
                    HybridEncryption.EncryptedData.fromBase64String(encoded);
            String decryptedText = HybridEncryption.decrypt(dataToDecrypt, privateKey);
            System.out.println("   Done\n");

            // 5. Show result
            System.out.println("5. Decrypted data:");
            System.out.println("   " + decryptedText + "\n");

            // 6. Verify
            System.out.println("6. Verification:");
            if (originalText.equals(decryptedText)) {
                System.out.println("   SUCCESS: Original and decrypted data match!");
            } else {
                System.out.println("   FAILED: Data mismatch");
            }

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demoWithStandardAPI() {
        try {
            System.out.println("=== Demo 2: Using Java Standard API Only ===\n");

            // 1. Load RSA keys
            System.out.println("1. Loading RSA keys...");
            PublicKey publicKey = HybridEncryptionStandard.loadPublicKey(PUBLIC_KEY_PATH);
            PrivateKey privateKey = HybridEncryptionStandard.loadPrivateKey(PRIVATE_KEY_PATH);
            System.out.println("   Done\n");

            // 2. Original data to encrypt
            String originalText = "This is confidential information. It will be securely protected by hybrid encryption.";
            System.out.println("2. Original data:");
            System.out.println("   " + originalText + "\n");

            // 3. Encrypt
            System.out.println("3. Encrypting...");
            HybridEncryptionStandard.EncryptedData encryptedData =
                    HybridEncryptionStandard.encrypt(originalText, publicKey);
            String encoded = encryptedData.toBase64String();
            System.out.println("   Done");
            System.out.println("   Encrypted data (Base64):");
            System.out.println("   " + encoded.substring(0, Math.min(100, encoded.length())) + "...\n");

            // 4. Decrypt
            System.out.println("4. Decrypting...");
            HybridEncryptionStandard.EncryptedData dataToDecrypt =
                    HybridEncryptionStandard.EncryptedData.fromBase64String(encoded);
            String decryptedText = HybridEncryptionStandard.decrypt(dataToDecrypt, privateKey);
            System.out.println("   Done\n");

            // 5. Show result
            System.out.println("5. Decrypted data:");
            System.out.println("   " + decryptedText + "\n");

            // 6. Verify
            System.out.println("6. Verification:");
            if (originalText.equals(decryptedText)) {
                System.out.println("   SUCCESS: Original and decrypted data match!");
            } else {
                System.out.println("   FAILED: Data mismatch");
            }

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demoWithGoogleTink() {
        try {
            System.out.println("=== Demo 3: Using Google Tink ===\n");

            // 0. Generate keys if they don't exist
            if (!HybridEncryptionTink.keysetFilesExist()) {
                System.out.println("0. Generating Tink keysets (first time only)...");
                HybridEncryptionTink.generateAndSaveKeys();
                System.out.println("   Done\n");
            }

            // 1. Load keysets
            System.out.println("1. Loading Tink keysets...");
            KeysetHandle publicKeysetHandle = HybridEncryptionTink.loadPublicKeyset("keys/tink_public_keyset.json");
            KeysetHandle privateKeysetHandle = HybridEncryptionTink.loadPrivateKeyset("keys/tink_private_keyset.json");
            System.out.println("   Done\n");

            // 2. Original data to encrypt
            String originalText = "This is confidential information. It will be securely protected by hybrid encryption.";
            System.out.println("2. Original data:");
            System.out.println("   " + originalText + "\n");

            // 3. Encrypt (very simple with Tink!)
            System.out.println("3. Encrypting...");
            String ciphertext = HybridEncryptionTink.encrypt(originalText, publicKeysetHandle);
            System.out.println("   Done");
            System.out.println("   Encrypted data (Base64):");
            System.out.println("   " + ciphertext.substring(0, Math.min(100, ciphertext.length())) + "...\n");

            // 4. Decrypt (very simple with Tink!)
            System.out.println("4. Decrypting...");
            String decryptedText = HybridEncryptionTink.decrypt(ciphertext, privateKeysetHandle);
            System.out.println("   Done\n");

            // 5. Show result
            System.out.println("5. Decrypted data:");
            System.out.println("   " + decryptedText + "\n");

            // 6. Verify
            System.out.println("6. Verification:");
            if (originalText.equals(decryptedText)) {
                System.out.println("   SUCCESS: Original and decrypted data match!");
            } else {
                System.out.println("   FAILED: Data mismatch");
            }

            // 7. Show Tink advantages
            System.out.println("\n7. Tink Advantages:");
            System.out.println("   - Automatic key management (IV, nonce generation)");
            System.out.println("   - Safe defaults (ECIES with AES-GCM)");
            System.out.println("   - Simple API (just encrypt/decrypt)");
            System.out.println("   - Built-in key rotation support");

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
