package nl.juraji.biliomi.utility.types;

import com.google.common.base.Joiner;
import nl.juraji.biliomi.utility.estreams.EBiStream;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class Templater {
  private static final String LIST_SEPARATOR = ", ";
  private static final String KEY_VALUE_SEPARATOR = ": ";
  private final HashMap<String, Supplier<Object>> replacements;
  private final String template;

  private Templater(String template) {
    this.replacements = new HashMap<>();
    this.template = template;
  }

  /**
   * Create a new template and return a new instance of the Templater
   *
   * @param pattern The pattern to use as template
   *                Can be null and set later
   * @return A new instance of Templater
   */
  public static Templater template(String pattern) {
    return new Templater(pattern);
  }

  /**
   * Simply concatenate a set of words separated by spaces
   *
   * @param words The words to concatenate
   * @return A String with all words separated by spaces
   */
  public static String spaced(Object... words) {
    return Joiner.on(' ').join(words);
  }

  /**
   * Add replacement binding
   *
   * @param key      The key to bind
   * @param callable A {@code Supplier<Object>} with the value to replace with
   * @return This instance
   */
  public Templater add(String key, Supplier<Object> callable) {
    replacements.put(key, callable);
    return this;
  }

  /**
   * Add replacement binding
   *
   * @param key The key to bind
   * @param o   An {@code Object} with the value to replace with
   * @return This instance
   */
  public Templater add(String key, Object o) {
    return add(key, () -> o);
  }

  /**
   * Get a previously added replacement
   * All Objects and Collections passed to add() are wrapped in callables
   *
   * @param key The key of the replacement to retrieve
   * @return A Callable containing the replacement object or task
   */
  public Supplier<Object> get(String key) {
    return replacements.getOrDefault(key, null);
  }

  /**
   * Apply the set replacements to the template
   *
   * @return The resulting string
   */
  public String apply() {
    MutableString buffer = new MutableString(template);

    EBiStream.from(replacements)
        .mapKey(Templater::prepareKey)
        .filterKey(buffer::contains)
        .mapValue(Supplier::get)
        .mapValue(Templater::checkIsList)
        .forEach(buffer::replace);

    return buffer.toString();
  }

  /**
   * @return The number of replacements in this templater
   */
  public int size() {
    return replacements.size();
  }

  /**
   * Check if a key is present within the template
   *
   * @param key The key to search
   * @return True when present else False
   */
  public boolean templateContainsKey(String key) {
    return this.template.contains(prepareKey(key));
  }

  /**
   * Removes any template text
   * @param subject The String to strip
   * @param template The template to remove
   * @return The stripped subject
   */
  public static String removeTemplate(String subject, String template) {
    if (StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(template)) {
      String[] templateParts = template.split("\\s?\\{\\{[a-zA-Z0-9]+}}\\s?");

      for (String part : templateParts) {
        subject = subject.replaceAll(part, " ");
      }
    }

    return subject;
  }

  /**
   * Internal method to support binding keys without {{ or }} marking
   *
   * @param key The key to prepare
   * @return The supplied key marked as {{KEY}}
   */
  private static String prepareKey(String key) {
    return new MutableString(key)
        .prependIfMissing("{{")
        .appendIfMissing("}}")
        .toString();
  }

  private static Object checkIsList(Object o) {
    if (o == null) {
      return "";
    }
    if (Collection.class.isAssignableFrom(o.getClass())) {
      return Joiner.on(LIST_SEPARATOR).join((Collection<?>) o);
    } else if (Map.class.isAssignableFrom(o.getClass())) {
      return Joiner.on(LIST_SEPARATOR).withKeyValueSeparator(KEY_VALUE_SEPARATOR).join((Map<?, ?>) o);
    } else {
      return o;
    }
  }
}
