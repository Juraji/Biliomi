package nl.juraji.biliomi.utility.cdi.producers;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.model.internal.yaml.usersettings.UserSettings;
import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.UpdateModeType;
import nl.juraji.biliomi.utility.calculate.ObjectGraphs;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.BotName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.ChannelName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UpdateMode;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UserSetting;
import nl.juraji.biliomi.utility.estreams.EStream;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
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
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
@Default
@Singleton
public class UserSettingsProducer {

  private UserSettings userSettings;
  private UpdateModeType updateMode;

  @PostConstruct
  private void initUserSettingsProducer() {
    File configurationDir = BiliomiContainer.getParameters().getConfigurationDir();

    if (configurationDir.exists()) {
      Collection<File> files = FileUtils.listFiles(configurationDir, new String[]{"yml"}, true);

      Yaml yamlInstance = new Yaml(new Constructor(UserSettings.class));
      userSettings = EStream.from(files)
          .filter(Objects::nonNull)
          .map(file -> yamlInstance.loadAs(new FileInputStream(file), UserSettings.class))
          .reduce(ObjectGraphs::mergeObjects)
          .orElse(null);

      if (userSettings == null) {
        throw new IllegalStateException("Failed to load configuration from " + configurationDir.getAbsolutePath());
      }

      String updateMode = userSettings.getBiliomi().getCore().getUpdateMode();
      // Somehow Yaml thinks "off" means "false"
      if (StringUtils.isEmpty(updateMode)) {
        this.updateMode = UpdateModeType.OFF;
      } else {
        this.updateMode = EnumUtils.toEnum(updateMode, UpdateModeType.class);
      }
    } else {
      this.userSettings = new UserSettings();
      ObjectGraphs.initializeObjectGraph(this.userSettings);
      this.updateMode = UpdateModeType.INSTALL;
      this.userSettings.getBiliomi().getCore().setUpdateMode(this.updateMode.toString());
    }
  }

  @Produces
  public UserSettings getUserSettings() {
    return userSettings;
  }

  @Produces
  @UserSetting("PRODUCER")
  public String getUserSetting(InjectionPoint injectionPoint) {
    Annotated annotated = injectionPoint.getAnnotated();

    if (annotated.isAnnotationPresent(UserSetting.class)) {
      String settingPath = annotated.getAnnotation(UserSetting.class).value();
      Object object = userSettings;
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
    return userSettings.getBiliomi().getTwitch().getLogin().getBotUsername().toLowerCase();
  }

  @Produces
  @ChannelName
  public String getChannelName() {
    return userSettings.getBiliomi().getTwitch().getLogin().getChannelUsername().toLowerCase();
  }
}
