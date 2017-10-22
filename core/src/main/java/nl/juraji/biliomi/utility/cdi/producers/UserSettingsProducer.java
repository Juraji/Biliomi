package nl.juraji.biliomi.utility.cdi.producers;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.config.core.YamlCoreSettings;
import nl.juraji.biliomi.config.core.biliomi.UpdateModeType;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.calculate.ObjectGraphs;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.BotName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.ChannelName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.CoreSetting;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UpdateMode;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
@Default
@Singleton
public class UserSettingsProducer {

  private YamlCoreSettings yamlCoreSettings;
  private UpdateModeType updateMode;

  @PostConstruct
  private void initUserSettingsProducer() {
    try {
      File coreYml = new File(BiliomiContainer.getParameters().getConfigurationDir(), "core.yml");
      Yaml yamlInstance = new Yaml(new Constructor(YamlCoreSettings.class));
      yamlCoreSettings = yamlInstance.loadAs(new FileInputStream(coreYml), YamlCoreSettings.class);

      String updateMode = yamlCoreSettings.getBiliomi().getCore().getUpdateMode();
      // Somehow Yaml thinks "off" means "false"
      if (StringUtils.isEmpty(updateMode)) {
        this.updateMode = UpdateModeType.OFF;
      } else {
        this.updateMode = EnumUtils.toEnum(updateMode, UpdateModeType.class);
      }
    } catch (FileNotFoundException e) {
      this.yamlCoreSettings = new YamlCoreSettings();
      ObjectGraphs.initializeObjectGraph(this.yamlCoreSettings);
      this.updateMode = UpdateModeType.INSTALL;
      this.yamlCoreSettings.getBiliomi().getCore().setUpdateMode(this.updateMode.toString());
    }
  }

  @Produces
  public YamlCoreSettings getYamlCoreSettings() {
    return yamlCoreSettings;
  }

  @Produces
  @CoreSetting("PRODUCER")
  public String getUserSetting(InjectionPoint injectionPoint) {
    Annotated annotated = injectionPoint.getAnnotated();

    if (annotated.isAnnotationPresent(CoreSetting.class)) {
      String settingPath = annotated.getAnnotation(CoreSetting.class).value();
      Object object = yamlCoreSettings;
      String[] split = settingPath.split("\\.");
      int c = 0;

      while (c < split.length) {
        try {
          object = PropertyUtils.getProperty(object, split[c]);
          c++;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
          LogManager.getLogger(getClass()).error("Failed retrieving setting " + settingPath, e);
          return null;
        }
      }

      return String.valueOf(object);
    }

    return null;
  }

  @Produces
  @UpdateMode
  public UpdateModeType getUpdateMode() {
    return updateMode;
  }

  @Produces
  @BotName
  public String getBotName() {
    return yamlCoreSettings.getBiliomi().getTwitch().getLogin().getBotUsername().toLowerCase();
  }

  @Produces
  @ChannelName
  public String getChannelName() {
    return yamlCoreSettings.getBiliomi().getTwitch().getLogin().getChannelUsername().toLowerCase();
  }
}
