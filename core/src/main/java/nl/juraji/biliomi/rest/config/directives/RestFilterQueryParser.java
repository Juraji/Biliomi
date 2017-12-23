package nl.juraji.biliomi.rest.config.directives;

import nl.juraji.biliomi.model.internal.rest.query.RestFilterDirective;
import nl.juraji.biliomi.model.internal.rest.query.RestFilterOperator;
import nl.juraji.biliomi.utility.calculate.NumberConverter;
import nl.juraji.biliomi.utility.calculate.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Juraji on 23-12-2017.
 * Biliomi
 */
public class RestFilterQueryParser {
  private static final Pattern QUERY_PREDICATE_PATTERN = Pattern.compile("^([a-z. ]+)\\s+([!]?[=~<>])\\s+(.*)$", Pattern.CASE_INSENSITIVE);
  private static final Pattern QUERY_QUOTE_PATTERN = Pattern.compile("^\"(.*)\"$");
  private static final String QUERY_AND_DELIMITER = "AND";
  private static final String QUERY_OR_DELIMITER = "OR";
  private static final String QUERY_NOT_OPERATOR = "!";

  private RestFilterQueryParser() {
    // Private constructor
  }

  public static Collection<RestFilterDirective> parseQuery(String query) throws RestFilterQueryException {
    return new RestFilterQueryParser().parse(query);
  }

  public Collection<RestFilterDirective> parse(String query) throws RestFilterQueryException {
    String[] queryArray = TextUtils.splitKeepDelimiter(query, true, "and", "or");
    final List<RestFilterDirective> directives = new ArrayList<>(queryArray.length);

    for (int i = 0; i < queryArray.length; i++) {
      RestFilterDirective directive = new RestFilterDirective();
      String predicate = queryArray[i].trim();

      if (QUERY_AND_DELIMITER.equalsIgnoreCase(predicate) || QUERY_OR_DELIMITER.equalsIgnoreCase(predicate)) {
        directive.setOrPrevious(QUERY_OR_DELIMITER.equalsIgnoreCase(predicate));
        try {
          predicate = queryArray[++i].trim();
        } catch (ArrayIndexOutOfBoundsException e) {
          throw new RestFilterQueryException("Got " + predicate.toUpperCase() + " operator but missing next predicate");
        }
      }

      Matcher predicateMatcher = QUERY_PREDICATE_PATTERN.matcher(predicate);
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
    if (valGroup.equalsIgnoreCase("null")) {
      return null;
    }

    // Booleans
    if (valGroup.equalsIgnoreCase("true")) {
      return true;
    } else if (valGroup.equalsIgnoreCase("false")) {
      return false;
    }

    // NumberConverter
    NumberConverter numberConverter = NumberConverter.asNumber(valGroup);
    if (!numberConverter.isNaN()) {
      return numberConverter.toDouble();
    }

    // Remove quotes;
    return valGroup.replaceAll(QUERY_QUOTE_PATTERN.pattern(), "$1");
  }

  private boolean isOpGroupNotted(String opGroup) {
    return opGroup.substring(0, 1).equals(QUERY_NOT_OPERATOR);
  }

  public static class RestFilterQueryException extends Exception {
    public RestFilterQueryException(String message) {
      super(message);
    }
  }
}
