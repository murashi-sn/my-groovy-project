# Hybrid Encryption Sample

Implementation example of hybrid encryption using Java Cipher API and Bouncy Castle.

## How It Works

1. **RSA (Public Key Cryptography)**: Encrypts the AES key
2. **AES-GCM (Symmetric Cryptography)**: Encrypts the actual data

## Setup

### 1. Generate RSA Key Pair

Generate private and public keys using OpenSSL:

```bash
# Create keys directory
mkdir keys

# Generate private key (PKCS#8 format)
openssl genpkey -algorithm RSA -out keys/private_key.pem -pkeyopt rsa_keygen_bits:2048

# Generate public key
openssl rsa -pubout -in keys/private_key.pem -out keys/public_key.pem
```

### 2. Setup Gradle Wrapper

Download the Gradle Wrapper jar file:

```bash
# Execute in Git Bash
curl -L -o gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar

# Grant execute permission to gradlew
chmod +x gradlew
```

### 3. How to Run

```bash
# Build
./gradlew build

# Run
./gradlew run
```

## Technologies Used

- Java 21
- Java Cipher API
- Bouncy Castle Provider
- Google Tink
- RSA 2048bit (OAEP padding)
- AES 256bit (GCM mode)

## Implementation Variants

This project includes three different implementations:

1. **HybridEncryption.java** - Using Bouncy Castle for PEM file handling
2. **HybridEncryptionStandard.java** - Using Java Standard API only (no external libraries)
3. **HybridEncryptionTink.java** - Using Google Tink (simplest implementation)

All three implementations are demonstrated when you run the application.
