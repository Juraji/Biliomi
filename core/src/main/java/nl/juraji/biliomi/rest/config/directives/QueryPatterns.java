package nl.juraji.biliomi.rest.config.directives;

import java.util.regex.Pattern;

/**
 * Created by Juraji on 21-5-2018.
 * Biliomi
 */
public class QueryPatterns {
    private final Pattern queryPredicatePattern = Pattern.compile("^([a-z. ]+)\\s+([!]?[=~<>])\\s+(.*)$", Pattern.CASE_INSENSITIVE);
    private final Pattern queryQuotePattern = Pattern.compile("^\"(.*)\"$");
    private final Pattern queryDelimiterPattern = Pattern.compile("((?<= and )|(?= and )|(?<= or )|(?= or ))", Pattern.CASE_INSENSITIVE);

    public Pattern getQueryPredicatePattern() {
        return queryPredicatePattern;
    }

    public Pattern getQueryDelimiterPattern() {
        return queryDelimiterPattern;
    }

    public Pattern getQueryQuotePattern() {
        return queryQuotePattern;
    }

    public boolean isAndDelimiter(String value) {
        return "AND".equalsIgnoreCase(value);
    }

    public boolean isOrDelimiter(String value) {
        return "OR".equalsIgnoreCase(value);
    }

    public boolean isNotOperator(String value) {
        return "!".equals(value);
    }

    public boolean isNullValue(String value) {
        return "null".equalsIgnoreCase(value);
    }

    public boolean isTrueValue(String value) {
        return "true".equalsIgnoreCase(value);
    }

    public boolean isFalseValue(String value) {
        return "false".equalsIgnoreCase(value);
    }
}
