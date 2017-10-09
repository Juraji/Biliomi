package nl.juraji.biliomi.utility.cdi.producers;

import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppData;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppDataValue;
import org.apache.logging.log4j.LogManager;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
@Singleton
public final class AppDataProducer {

  private final Properties properties = new Properties();

  @PostConstruct
  private void initAppDataProducer() {
    String settingsFile = "/AppData.properties";
    InputStream stream = AppDataProducer.class.getResourceAsStream(settingsFile);

    try {
      properties.load(stream);
    } catch (IOException e) {
      LogManager.getLogger(getClass()).error("Failed to load application data file {}", settingsFile, e);
    }
  }

  @Produces
  @AppData
  public Properties getAppData() {
    return properties;
  }

  @Produces
  @AppDataValue("PRODUCER")
  public String getAppDataValue(InjectionPoint injectionPoint) {
    Annotated annotated = injectionPoint.getAnnotated();
    String key = null;
    String value = null;

    if (annotated.isAnnotationPresent(AppDataValue.class)) {
      AppDataValue annotation = annotated.getAnnotation(AppDataValue.class);
      key = annotation.value();
      value = properties.getProperty(key);
    }

    if (value == null) {
      throw new IllegalArgumentException("No AppData value found for key " + key);
    }

    return value;
  }
}
