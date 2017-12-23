package nl.juraji.biliomi.utility.calculate;

import com.google.common.collect.Iterators;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class TextUtils {
  private static final String COMMA_LIST_SEPARATOR = ", ";

  private TextUtils() {
  }

  /**
   * Chunkify strings to a set length, but do not break words
   *
   * @param string The string to chunkify
   * @param length The max length of the chunks
   * @return A list containing the resulting chunks
   */
  public static List<String> chunkify(String string, int length) {
    List<String> chunks = new ArrayList<>();

    if (StringUtils.isEmpty(string) || string.length() <= length) {
      chunks.add(string);
      return chunks;
    }

    Iterator<String> words = Iterators.forArray(string.split(" "));
    int chunkCount = (int) Math.ceil(((double) string.length()) / length);

    IntStream.rangeClosed(1, chunkCount).forEach(ci -> {
      StringBuilder newChunk = new StringBuilder();

      while (newChunk.length() < length) {
        if (!words.hasNext()) break;
        newChunk.append(" ").append(words.next());
      }

      if (StringUtils.isNotEmpty(newChunk)) chunks.add(newChunk.toString().trim());
    });

    return chunks;
  }

  /**
   * Merge a collection of strings separating each item with a comma
   *
   * @param collection The collection to merge
   * @return The resulting string. May be empty if the collection was empty or null
   */
  @NotNull
  public static String commaList(Collection<String> collection) {
    if (collection == null || collection.isEmpty()) {
      return "";
    }
    return commaList(collection.stream());
  }

  /**
   * Merge a stream of strings separating each item with a comma
   *
   * @param stream The stream to merge
   * @return The resulting string. May be empty if the stream was empty
   */
  public static String commaList(Stream<String> stream) {
    return stream
        .reduce((l, r) -> l + COMMA_LIST_SEPARATOR + r)
        .orElse("");
  }

  /**
   * Split a string on the given delimiters while also placing the delimiters themselves in the result array.
   *
   * @param originalString The string to splut
   * @param delimiters     One or more delimiters
   * @return The resulting array
   */
  public static String[] splitKeepDelimiter(String originalString, boolean caseInsensitive, String... delimiters) {
    final String delimiterTemplate = "(?<=%1$s)|(?=%1$s)";

    if (caseInsensitive) {
      delimiters = Arrays.stream(delimiters)
          .map(s -> "(" + s + "|" + s.toUpperCase() + ")")
          .toArray(String[]::new);
    }

    return Arrays.stream(delimiters)
        .map(s -> String.format(delimiterTemplate, s))
        .reduce((l, r) -> l + "|" + r)
        .map(s -> originalString.split("(" + s + ")"))
        .orElse(new String[]{});
  }
}
