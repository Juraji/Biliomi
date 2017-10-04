package nl.juraji.biliomi.utility.cdi.producers;

import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.*;
import nl.juraji.biliomi.utility.settings.AppData;
import nl.juraji.biliomi.utility.settings.UpdateModeType;
import nl.juraji.biliomi.utility.settings.UserSettings;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
@Singleton
public final class AppSettingProviderProducer {

  @Inject
  private UserSettings userSettings;

  @Inject
  private AppData appData;

  @Produces
  @UserSetting("PRODUCER")
  public String getUserSettingValue(InjectionPoint injectionPoint) {
    Annotated annotated = injectionPoint.getAnnotated();
    String key = null;
    String value = null;

    if (annotated.isAnnotationPresent(UserSetting.class)) {
      UserSetting annotation = annotated.getAnnotation(UserSetting.class);
      key = annotation.value();
      value = userSettings.getValue(key);
    }

    if (value == null) {
      throw new IllegalArgumentException("No setting value set for key " + key);
    }

    return value;
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
      value = appData.getValue(key);
    }

    if (value == null) {
      throw new IllegalArgumentException("No AppData value found for key " + key);
    }

    return value;
  }

  @Produces
  @UpdateMode
  public UpdateModeType getUpdateMode() {
    final String updateModeSetting = userSettings.getValue("biliomi.core.updateMode", "OFF");

    // Somehow Yaml thinks "off" means "false"
    if ("false".equalsIgnoreCase(updateModeSetting)) {
      return UpdateModeType.OFF;
    }

    return EnumUtils.toEnum(updateModeSetting, UpdateModeType.class);
  }

  @Produces
  @BotName
  public String getBotName() {
    return userSettings.getValue("biliomi.twitch.login.botUsername");
  }

  @Produces
  @ChannelName
  public String getChannelName() {
    return userSettings.getValue("biliomi.twitch.login.channelUsername");
  }
}
