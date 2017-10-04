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
public final class PasswordEncryption {
  private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
  private static final String SEC_RANDOM_TYPE = "SHA1PRNG";
  private static final int DERIVED_KEY_LENGTH = 160;
  private static final int HASH_ITERATIONS = 20000;

  public static boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] encryptedAttemptedPassword = encrypt(attemptedPassword, salt);
    return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
  }

  public static byte[] encrypt(String password, byte[] salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, DERIVED_KEY_LENGTH);
    SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
    return f.generateSecret(spec).getEncoded();
  }

  public static byte[] generateSalt() throws NoSuchAlgorithmException {
    SecureRandom random = SecureRandom.getInstance(SEC_RANDOM_TYPE);
    byte[] salt = new byte[8];
    random.nextBytes(salt);
    return salt;
  }
}
