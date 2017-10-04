package nl.juraji.biliomi.utility.settings;

import java.util.Map;

/**
 * Created by Juraji on 18-4-2017.
 * Biliomi v3
 */
public interface AppSettingProvider {
  Object getObjectValue(String key);

  default String getValue(String key) {
    Object objectValue = getObjectValue(key);

    // NULL toString will actually be "null", this statement prevents this
    return (objectValue == null ? null : String.valueOf(objectValue));
  }

  default String getValue(String key, String defaultValue) {
    String value = getValue(key);
    return (value == null ? defaultValue : value);
  }

  default boolean getBooleanValue(String key) {
    return "true".equals(getValue(key));
  }

  static boolean isInvalidMapProperty(String key, Class<?> targetClass, Map<String, Object> map) {
    return !map.containsKey(key) || !targetClass.isAssignableFrom(map.get(key).getClass());
  }
}
