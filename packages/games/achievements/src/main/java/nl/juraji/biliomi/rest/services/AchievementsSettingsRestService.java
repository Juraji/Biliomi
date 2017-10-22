package nl.juraji.biliomi.rest.services;

import nl.juraji.biliomi.model.games.AchievementsSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
@Path("/games/settings/achievements")
public class AchievementsSettingsRestService extends SettingsModelRestService<AchievementsSettings> {

  @Override
  public AchievementsSettings getEntity() {
    return settingsService.getSettings(AchievementsSettings.class);
  }

  @Override
  public AchievementsSettings updateEntity(AchievementsSettings e) {
    AchievementsSettings settings = settingsService.getSettings(AchievementsSettings.class);

    settings.setAchievementsEnabled(e.isAchievementsEnabled());

    settingsService.save(settings);
    return settings;
  }
}
