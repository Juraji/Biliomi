package nl.juraji.biliomi.utility.settings;

import org.apache.logging.log4j.LogManager;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Juraji on 18-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class AppData implements AppSettingProvider {
  private static final String SETTINGS_FILE = "/AppData.properties";
  private final Properties properties = new Properties();

  public AppData() {
    InputStream stream = AppData.class.getResourceAsStream(SETTINGS_FILE);

    try {
      properties.load(stream);
    } catch (IOException e) {
      LogManager.getLogger(getClass()).error("Failed to load application data file {}", SETTINGS_FILE, e);
    }
  }

  @Override
  public Object getObjectValue(String key) {
    return properties.get(key);
  }
}
