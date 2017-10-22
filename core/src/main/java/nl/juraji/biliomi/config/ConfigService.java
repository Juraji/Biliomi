package nl.juraji.biliomi.config;

import nl.juraji.biliomi.BiliomiContainer;
import org.apache.logging.log4j.LogManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
public abstract class ConfigService<T> {

  protected final T config;

  public ConfigService(String configPath, Class<T> constructorClass) {
    T loadedConfig = null;
    File file = new File(BiliomiContainer.getParameters().getConfigurationDir(), configPath);
    Yaml yaml = new Yaml(new Constructor(constructorClass));

    try {
      loadedConfig = yaml.loadAs(new FileInputStream(file), constructorClass);
    } catch (FileNotFoundException e) {
      LogManager.getLogger(getClass().getName()).error("Failed loading module configuration from " + file.getAbsolutePath());
    }

    this.config = loadedConfig;
  }
}
