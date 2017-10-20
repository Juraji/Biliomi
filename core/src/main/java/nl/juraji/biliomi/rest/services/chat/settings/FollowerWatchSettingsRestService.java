package nl.juraji.biliomi.rest.services.chat.settings;

import nl.juraji.biliomi.model.core.settings.FollowerWatchSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/chat/settings/followerwatch")
public class FollowerWatchSettingsRestService extends SettingsModelRestService<FollowerWatchSettings> {

  @Override
  public FollowerWatchSettings getEntity() {
    return settingsService.getSettings(FollowerWatchSettings.class);
  }

  @Override
  public FollowerWatchSettings updateEntity(FollowerWatchSettings e) {
    FollowerWatchSettings settings = settingsService.getSettings(FollowerWatchSettings.class);

    settings.setReward(e.getReward());

    settingsService.save(settings);
    return settings;
  }
}
