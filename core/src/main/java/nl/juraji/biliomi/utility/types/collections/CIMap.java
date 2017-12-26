package nl.juraji.biliomi.utility.types.collections;

import nl.juraji.biliomi.utility.estreams.EBiStream;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Juraji on 4-5-2017.
 * Biliomi v3
 * <p>
 * Case insensitive HashMap
 */
public class CIMap<V> extends HashMap<String, V> {

  @Override
  public V get(Object key) {
    return super.get(lcKey(key));
  }

  @Override
  public boolean containsKey(Object key) {
    return super.containsKey(lcKey(key));
  }

  @Override
  public V put(String key, V value) {
    return super.put(lcKey(key), value);
  }

  @Override
  public void putAll(Map<? extends String, ? extends V> m) {
    Map<String, ? extends V> map = EBiStream.from(m).mapKey(this::lcKey).toMap();
    super.putAll(map);
  }

  public void putIfNotNull(String key, V value) {
    if (value!=null) {
      put(key, value);
    }
  }

  @Override
  public V remove(Object key) {
    return super.remove(lcKey(key));
  }

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    return super.getOrDefault(lcKey(key), defaultValue);
  }

  @Override
  public V putIfAbsent(String key, V value) {
    return super.putIfAbsent(lcKey(key), value);
  }

  @Override
  public boolean remove(Object key, Object value) {
    return super.remove(lcKey(key), value);
  }

  @Override
  public boolean replace(String key, V oldValue, V newValue) {
    return super.replace(lcKey(key), oldValue, newValue);
  }

  @Override
  public V replace(String key, V value) {
    return super.replace(lcKey(key), value);
  }

  @Override
  public V computeIfAbsent(String key, Function<? super String, ? extends V> mappingFunction) {
    return super.computeIfAbsent(lcKey(key), mappingFunction);
  }

  @Override
  public V computeIfPresent(String key, BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
    return super.computeIfPresent(lcKey(key), remappingFunction);
  }

  @Override
  public V compute(String key, BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
    return super.compute(lcKey(key), remappingFunction);
  }

  @Override
  public V merge(String key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    return super.merge(lcKey(key), value, remappingFunction);
  }

  private String lcKey(Object key) {
    if (key == null) {
      return null;
    }
    return ((String) key).toLowerCase();
  }
}
