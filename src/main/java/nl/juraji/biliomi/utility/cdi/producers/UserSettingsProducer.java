package nl.juraji.biliomi.utility.cdi.producers;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.model.internal.yaml.usersettings.UserSettings;
import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.UpdateModeType;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.calculate.DeepMerge;
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

  @PostConstruct
  private void initUserSettingsProducer() {
    File configurationDir = BiliomiContainer.getParameters().getConfigurationDir();
    Yaml yamlInstance = new Yaml(new Constructor(UserSettings.class));

    Collection<File> files = FileUtils.listFiles(configurationDir, new String[]{"yml"}, true);

    userSettings = EStream.from(files)
        .filter(Objects::nonNull)
        .map(file -> yamlInstance.loadAs(new FileInputStream(file), UserSettings.class))
        .reduce(DeepMerge::mergePojo)
        .orElse(null);
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
        }
      }

      return String.valueOf(object);
    }

    return null;
  }

  @Produces
  @UpdateMode
  public UpdateModeType getUpdateMode() {
    String updateMode = userSettings.getBiliomi().getCore().getUpdateMode();

    // Somehow Yaml thinks "off" means "false"
    if (StringUtils.isEmpty(updateMode)) {
      return UpdateModeType.OFF;
    }

    return EnumUtils.toEnum(updateMode, UpdateModeType.class);
  }

  @Produces
  @BotName
  public String getBotName() {
    return userSettings.getBiliomi().getTwitch().getLogin().getBotUsername();
  }

  @Produces
  @ChannelName
  public String getChannelName() {
    return userSettings.getBiliomi().getTwitch().getLogin().getChannelUsername();
  }
}
