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
    private final boolean includeSymbols;

    public TokenGenerator(int length) {
        this(length, true);
    }

    public TokenGenerator(int length, boolean includeSymbols) {
        this.includeSymbols = includeSymbols;
        this.random = new SecureRandom();
        this.length = length;
    }

    public String generate() {
        final MutableString token = new MutableString();
        final String chars;

        if (includeSymbols) {
            chars = CHARS + SYMBOLS;
        } else {
            chars = CHARS;
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
