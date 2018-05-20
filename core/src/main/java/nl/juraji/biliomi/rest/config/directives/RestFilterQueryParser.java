package nl.juraji.biliomi.rest.config.directives;

import nl.juraji.biliomi.model.internal.rest.query.RestFilterDirective;
import nl.juraji.biliomi.model.internal.rest.query.RestFilterOperator;
import nl.juraji.biliomi.utility.calculate.NumberConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by Juraji on 23-12-2017.
 * Biliomi
 */
public class RestFilterQueryParser {

    private final QueryPatterns patterns;

    private RestFilterQueryParser() {
        this.patterns = new QueryPatterns();
    }

    public static Collection<RestFilterDirective> parseQuery(String query) throws RestFilterQueryException {
        return new RestFilterQueryParser().parse(query);
    }

    public Collection<RestFilterDirective> parse(String query) throws RestFilterQueryException {
        String[] queryArray = this.splitQuery(query);
        final List<RestFilterDirective> directives = new ArrayList<>(queryArray.length);

        for (int i = 0; i < queryArray.length; i++) {
            RestFilterDirective directive = new RestFilterDirective();
            String predicate = queryArray[i];

            if (patterns.isAndDelimiter(predicate) || patterns.isOrDelimiter(predicate)) {
                directive.setOrPrevious(patterns.isOrDelimiter(predicate));
                try {
                    predicate = queryArray[++i];
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new RestFilterQueryException("Got " + predicate.toUpperCase() + " operator but missing next predicate");
                }
            }

            Matcher predicateMatcher = patterns.getQueryPredicatePattern().matcher(predicate);
            if (!predicateMatcher.matches()) {
                throw new RestFilterQueryException("Invalid query predicate: " + predicate);
            }

            directive.setProperty(predicateMatcher.group(1));
            directive.setOperator(convertOperator(predicateMatcher.group(2)));
            directive.setValue(getRealValue(predicateMatcher.group(3)));
            directive.setNegative(isOpGroupNotted(predicateMatcher.group(2)));

            directives.add(directive);
        }

        return directives;
    }

    private String[] splitQuery(String query) {
        String[] parts = patterns.getQueryDelimiterPattern().split(query);

        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }

        return parts;
    }

    private RestFilterOperator convertOperator(String opGroup) throws RestFilterQueryException {
        if (isOpGroupNotted(opGroup)) {
            opGroup = opGroup.substring(1, 2);
        }

        switch (opGroup) {
            case "=":
                return RestFilterOperator.EQUALS;
            case "~":
                return RestFilterOperator.CONTAINS;
            case "<":
                return RestFilterOperator.LESSER_THAN;
            case ">":
                return RestFilterOperator.GREATER_THAN;
            default:
                throw new RestFilterQueryException("Invalid operator: " + opGroup);
        }
    }

    private Object getRealValue(String valGroup) {
        // Nulls
        if (patterns.isNullValue(valGroup)) {
            return null;
        }

        // Booleans
        if (patterns.isTrueValue(valGroup)) {
            return true;
        } else if (patterns.isFalseValue(valGroup)) {
            return false;
        }

        // NumberConverter
        NumberConverter numberConverter = NumberConverter.asNumber(valGroup);
        if (!numberConverter.isNaN()) {
            return numberConverter.toDouble();
        }

        // Remove quotes;
        return valGroup.replaceAll(patterns.getQueryQuotePattern().pattern(), "$1");
    }

    private boolean isOpGroupNotted(String opGroup) {
        return patterns.isNotOperator(opGroup.substring(0, 1));
    }

    public static class RestFilterQueryException extends Exception {
        public RestFilterQueryException(String message) {
            super(message);
        }
    }
}
