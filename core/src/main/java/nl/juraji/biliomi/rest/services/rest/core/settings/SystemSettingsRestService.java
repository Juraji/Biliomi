package nl.juraji.biliomi.rest.services.rest.core.settings;

import nl.juraji.biliomi.model.core.settings.SystemSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/core/settings/system")
public class SystemSettingsRestService extends SettingsModelRestService<SystemSettings> {

  @Override
  public SystemSettings getEntity() {
    return settingsService.getSettings(SystemSettings.class);
  }

  @Override
  public SystemSettings updateEntity(SystemSettings e) {
    SystemSettings settings = settingsService.getSettings(SystemSettings.class);

    settings.setMuted(e.isMuted());
    settings.setEnableWhispers(e.isEnableWhispers());

    settingsService.save(settings);
    return settings;
  }
}
