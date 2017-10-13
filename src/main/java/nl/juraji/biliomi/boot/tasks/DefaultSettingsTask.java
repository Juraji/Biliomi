package nl.juraji.biliomi.boot.tasks;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.settings.Settings;
import nl.juraji.biliomi.model.core.settings.SettingsDao;
import nl.juraji.biliomi.utility.factories.reflections.ReflectionUtils;
import nl.juraji.biliomi.utility.types.AppParameters;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.Objects;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
@Default
@SetupTaskPriority(priority = 2)
public class DefaultSettingsTask implements SetupTask {

  @Inject
  private Logger logger;

  @Inject
  private SettingsDao settingsDao;

  @Inject
  private AuthTokenDao authTokenDao;

  private final AppParameters parameters;

  public DefaultSettingsTask() {
    parameters = BiliomiContainer.getParameters();
  }

  @Override
  public void install() {
    // Load all Settings objects and save their initial state to the database
    ReflectionUtils.forClassPackage(Settings.class)
        .subTypes(Settings.class)
        .mapToBiEStream(settingsDao::getSettings)
        .mapValue((clazz, obj) -> (obj == null ? clazz.getDeclaredConstructor().newInstance() : obj))
        .forEach((clazz, obj) -> {
          obj.setDefaultValues();
          settingsDao.save(obj);
          logger.info("Saved {}", clazz.getSimpleName());
        });

    if (parameters.isResetAuth()) {
      authTokenDao.delete(authTokenDao.getList());
    }
  }

  @Override
  public void update() {
    // Load all Settings objects and save their initial state to the database if no entry exists
    ReflectionUtils.forClassPackage(Settings.class)
        .subTypes(Settings.class)
        .mapToBiEStream(settingsDao::getSettings)
        .mapValue((clazz, obj) -> (obj == null ? clazz.getDeclaredConstructor().newInstance() : null))
        .filterValue(Objects::nonNull)
        .forEach((clazz, obj) -> {
          obj.setDefaultValues();
          settingsDao.save(obj);
          logger.info("Saved {}", clazz.getSimpleName());
        });

    if (parameters.isResetAuth()) {
      authTokenDao.delete(authTokenDao.getList());
    }
  }

  @Override
  public String getDisplayName() {
    return "Install/update default settings";
  }
}
