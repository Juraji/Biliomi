package nl.juraji.biliomi.utility.types;

import java.security.SecureRandom;

/**
 * Created by Juraji on 19-5-2017.
 * Biliomi v3
 */
public class TokenGenerator {
  private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final String SYMBOLS = "!@#$%^&*_=+-/";

  private final SecureRandom random;
  private final int length;
  private final boolean noSymbols;

  public TokenGenerator(int length) {
    this(length, false);
  }

  public TokenGenerator(int length, boolean noSymbols) {
    this.noSymbols = noSymbols;
    this.random = new SecureRandom();
    this.length = length;
  }

  public String generate() {
    final MutableString token = new MutableString();
    final String chars;

    if (noSymbols) {
      chars = CHARS;
    } else {
      chars = CHARS + SYMBOLS;
    }

    final int charCount = chars.length();
    int c = 0;

    while (c < this.length) {
      token.append(chars.charAt(random.nextInt(charCount)));
      c++;
    }

    return token.toString();
  }
}
