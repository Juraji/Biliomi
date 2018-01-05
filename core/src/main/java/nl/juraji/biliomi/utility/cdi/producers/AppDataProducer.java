package nl.juraji.biliomi.utility.cdi.producers;

import nl.juraji.biliomi.utility.calculate.NumberConverter;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppData;
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
  @AppData("PRODUCER")
  public String getAppDataValueAsString(InjectionPoint injectionPoint) {
    Annotated annotated = injectionPoint.getAnnotated();
    String key = null;
    String value = null;

    if (annotated.isAnnotationPresent(AppData.class)) {
      AppData annotation = annotated.getAnnotation(AppData.class);
      key = annotation.value();
      value = properties.getProperty(key);
    }

    if (value == null) {
      throw new IllegalArgumentException("No AppData value found for key " + key);
    }

    return value;
  }

  @Produces
  @AppData("PRODUCER")
  public Long getAppDataValueAsLong(InjectionPoint injectionPoint) {
    String appDataValueAsString = getAppDataValueAsString(injectionPoint);
    return NumberConverter.asNumber(appDataValueAsString).toLong();
  }

  @Produces
  @AppData("PRODUCER")
  public Integer getAppDataValueAsInteger(InjectionPoint injectionPoint) {
    String appDataValueAsString = getAppDataValueAsString(injectionPoint);
    return NumberConverter.asNumber(appDataValueAsString).toInteger();
  }
}
