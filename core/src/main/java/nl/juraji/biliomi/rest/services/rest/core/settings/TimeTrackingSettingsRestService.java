package nl.juraji.biliomi.rest.services.rest.core.settings;

import nl.juraji.biliomi.model.core.settings.TimeTrackingSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/core/settings/timetracking")
public class TimeTrackingSettingsRestService extends SettingsModelRestService<TimeTrackingSettings> {

  @Override
  public TimeTrackingSettings getEntity() {
    return settingsService.getSettings(TimeTrackingSettings.class);
  }

  @Override
  public TimeTrackingSettings updateEntity(TimeTrackingSettings e) {
    TimeTrackingSettings settings = settingsService.getSettings(TimeTrackingSettings.class);

    settings.setTrackOnline(e.isTrackOnline());
    settings.setTrackOffline(e.isTrackOffline());

    settingsService.save(settings);
    return settings;
  }
}
