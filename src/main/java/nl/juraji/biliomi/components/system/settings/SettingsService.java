package nl.juraji.biliomi.components.system.settings;

import nl.juraji.biliomi.model.core.settings.SettingsDao;
import nl.juraji.biliomi.model.core.settings.Settings;
import nl.juraji.biliomi.model.core.settings.SystemSettings;
import nl.juraji.biliomi.utility.events.eventemitter.EventEmitter;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.Consumer;

/**
 * Created by robin.
 * april 2017
 */
@Default
@Singleton
public class SettingsService {
  private final EventEmitter<Settings> onSaveEvent = new EventEmitter<>();

  @Inject
  private SettingsDao settingsDao;

  public SystemSettings getSystemSettings() {
    return settingsDao.getSettings(SystemSettings.class);
  }

  public <T extends Settings> T getSettings(Class<T> type) {
    return settingsDao.getSettings(type);
  }

  public <T extends Settings> T getSettings(Class<T> type, Consumer<T> onUpdated) {
    //noinspection unchecked
    onSaveEvent.subscribe(type, s -> onUpdated.accept((T) s));
    return settingsDao.getSettings(type);
  }

  public void save(Settings settings) {
    settingsDao.save(settings);
    onSaveEvent.emit(settings);
  }
}
