package nl.juraji.biliomi.utility.factories;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 26-6-2017.
 * Biliomi v3
 */
public class ModelUtils {

  public static <T> Set<T> initCollection(Set<T> collection) {
    if (collection == null) {
      return new HashSet<>();
    }
    return collection;
  }

  public static <T> List<T> initCollection(List<T> collection) {
    if (collection == null) {
      return new ArrayList<>();
    }
    return collection;
  }

  public static <K, V> Map<K, V> initCollection(Map<K, V> collection) {
    if (collection == null) {
      return new HashMap<>();
    }
    return collection;
  }

  @SafeVarargs
  public static <T> Set<T> setOf(T... entries) {
    return Arrays.stream(entries).collect(Collectors.toSet());
  }

  @SafeVarargs
  public static <T> List<T> listOf(T... entries) {
    return Arrays.stream(entries).collect(Collectors.toList());
  }

  public static <K, V> Map<K, V> mapWith(K key, V value) {
    Map<K, V> map = new HashMap<>();
    map.put(key, value);
    return map;
  }
}
