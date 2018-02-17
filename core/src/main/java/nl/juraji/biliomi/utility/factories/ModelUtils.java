package nl.juraji.biliomi.utility.factories;

import java.util.*;

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

  public static class MapEntry<K, V> implements Map.Entry<K, V> {
    private K key;
    private V value;

    public MapEntry(K key, V value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public K getKey() {
      return key;
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    public V setValue(V value) {
      return value;
    }

    @Override
    public boolean equals(Object o) {
      return (o instanceof MapEntry && ((MapEntry) o).key == key && ((MapEntry) o).value == value);
    }

    @Override
    public int hashCode() {
      return key.hashCode() + value.hashCode() / 2;
    }
  }
}
