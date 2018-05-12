package nl.juraji.biliomi.utility.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

/**
 * Created by Juraji on 16-6-2017.
 * Biliomi v3
 */
public final class PasswordEncryptor {
    private final String algorithm;
    private final String secRandomType;
    private final int derivedKeyLength;
    private final int hashIterations;

    public PasswordEncryptor() {
        algorithm = "PBKDF2WithHmacSHA1";
        secRandomType = "SHA1PRNG";
        derivedKeyLength = 160;
        hashIterations = 20000;
    }

    public boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encryptedAttemptedPassword = encrypt(attemptedPassword, salt);
        return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
    }

    public byte[] encrypt(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, hashIterations, derivedKeyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
        return f.generateSecret(spec).getEncoded();
    }

    public byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance(secRandomType);
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return salt;
    }
}
