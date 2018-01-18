package nl.juraji.biliomi.io.web;

import com.google.common.base.Splitter;
import nl.juraji.biliomi.utility.types.MutableString;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juraji on 25-5-2017.
 * Biliomi v3
 */
public final class Url {
  private static final String QUERY_ENCODING = "UTF-8";
  private static final String QUERY_IDENTIFIER = "?";
  private static final char QUERY_PAIR_SEPARATOR = '&';
  private static final char QUERY_KEY_VALUE_SEPARATOR = '=';

  private static final char ELEMENT_SEPARATOR = '/';

  private String uri;

  public Url(String baseUri, Object... pathElements) {
    if (pathElements.length > 0) {
      this.uri = baseUri + ELEMENT_SEPARATOR + Arrays.stream(pathElements)
          .map(String::valueOf)
          .reduce((l, r) -> l + ELEMENT_SEPARATOR + r)
          .orElse("");
    } else {
      this.uri = baseUri;
    }
  }

  public static Url url(String baseUri, Object... pathElements) {
    return new Url(baseUri, pathElements);
  }

  public Url withQuery(Map<String, Object> query) {
    this.uri = appendQueryString(this.uri, query);
    return this;
  }

  @Override
  public String toString() {
    return this.uri;
  }

  /**
   * Unpack the querystring from the given url
   * Returns an empty map if there is no querystring
   *
   * @param url The url to parse
   * @return A Map containing the key-value pairs
   */
  public static Map<String, String> unpackQueryString(String url) {
    return unpackQueryString(url, false);
  }

  /**
   * Unpack the querystring from the given url
   * Returns an empty map if there is no querystring
   *
   * @param url               The url to parse
   * @param isPureQueryString Supply true if the url is just the query string or false for complete urls
   * @return A Map containing the key-value pairs
   */
  public static Map<String, String> unpackQueryString(String url, boolean isPureQueryString) {
    if (url != null) {
      String query = null;

      if (isPureQueryString) {
        query = url;
      } else {
        int qidIndex = url.indexOf(QUERY_IDENTIFIER);
        if (qidIndex > -1) {
          query = url.substring(qidIndex + 1);
        }
      }

      if (query != null) {
        query = decode(query);
        return Splitter
            .on(QUERY_PAIR_SEPARATOR)
            .trimResults()
            .withKeyValueSeparator(QUERY_KEY_VALUE_SEPARATOR)
            .split(query);
      }
    }
    return new HashMap<>();
  }

  /**
   * Encode the given Map to a url querystring.
   *
   * @param query A map containing the key-value pairs to pack
   * @return The resulting querystring (without ?) or an empty string if there were no values
   */
  public static String createQueryString(Map<String, Object> query) {
    return query.entrySet().stream()
        .map(e -> urlEncodeUTF8(e.getKey()) + QUERY_KEY_VALUE_SEPARATOR + urlEncodeUTF8(String.valueOf(e.getValue())))
        .reduce((q1, q2) -> q1 + QUERY_PAIR_SEPARATOR + q2)
        .orElse("");
  }

  /**
   * Encode the given Map to a url querystring and append it to the base url.
   * Inserts a "?" if its missing
   *
   * @param uri   The uri to append the query to
   * @param query A map containing the key-value pairs to pack
   * @return The resulting uri
   */
  public static String appendQueryString(String uri, Map<String, Object> query) {
    return new MutableString(uri)
        .appendIfMissing(QUERY_IDENTIFIER)
        .append(createQueryString(query))
        .toString();
  }

  /**
   * Encode a string to make it url-safe
   *
   * @param s The string to encode
   * @return The encoded representation of the input string
   */
  public static String urlEncodeUTF8(String s) {
    try {
      return URLEncoder.encode(s, QUERY_ENCODING);
    } catch (UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Decode an uri (component) with UTF-8 encoding
   *
   * @param uri      The uri to decode
   * @return The decoded uri or null on failure
   */
  public static String decode(String uri) {
    return decode(uri, "UTF-8");
  }

  /**
   * Decode an uri (component)
   *
   * @param uri      The uri to decode
   * @param encoding The encoding to use
   * @return The decoded uri or null on failure
   */
  public static String decode(String uri, String encoding) {
    try {
      return URLDecoder.decode(uri, encoding);
    } catch (UnsupportedEncodingException ignored) {
      return null;
    }
  }
}
