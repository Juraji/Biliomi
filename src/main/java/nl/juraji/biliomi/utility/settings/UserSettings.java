package nl.juraji.biliomi.utility.settings;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.utility.calculate.MapUtils;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.types.MutableString;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Juraji on 17-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class UserSettings implements AppSettingProvider {
  private final Object yamlData;

  public UserSettings() {
    Object data = null;

    try {
      data = loadConfig();
    } catch (Exception e) {
      LogManager.getLogger(getClass()).error("Failed to load component settings files", e);
      BiliomiContainer.getContainer().shutdownInError();
    }

    yamlData = data;
  }

  /**
   * Retrieve a value from the application settings file using dot separated paths
   *
   * @param key The dot separated path to the value to retrieve
   * @return The found value as String or null
   */
  @Override
  public Object getObjectValue(String key) {
    if (this.yamlData == null) {
      return null;
    }

    // Last in key should be "." to trigger lookback and fetch the value
    final String finalKey = key + '.';

    MutableString segment = new MutableString();
    Object yamlValue = this.yamlData;
    int l = finalKey.length();
    int i = 0;

    while (i < l) {
      char c = finalKey.charAt(i);

      if (c == '.') {
        if (yamlValue instanceof Map) {
          yamlValue = ((Map) yamlValue).get(segment.toString());
        } else {
          yamlValue = null;
          break;
        }
        segment.clear();
      } else {
        segment.append(c);
      }

      i++;
    }

    return yamlValue;
  }

  private Object loadConfig() {
    File configurationDir = BiliomiContainer.getParameters().getConfigurationDir();
    Yaml yamlInst = new Yaml(new SafeConstructor());

    Map<String, Object> data = new HashMap<>();
    // Load and merge configuration
    Collection<File> files = FileUtils.listFiles(configurationDir, new String[]{"yml"}, true);
    EStream.from(files)
        .filter(Objects::nonNull)
        .map(file -> loadFile(file, yamlInst))
        .forEach(map -> MapUtils.deepMerge(data, map));

    return data;
  }

  private Map<String, Object> loadFile(File file, Yaml yamlInst) throws FileNotFoundException {
    //noinspection unchecked
    return (Map<String, Object>) yamlInst.load(new FileInputStream(file));
  }
}
