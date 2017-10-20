package nl.juraji.biliomi.rest.services.rest.games;

import nl.juraji.biliomi.model.games.AdventureSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/games/settings/adventure")
public class AdventureSettingsRestService extends SettingsModelRestService<AdventureSettings> {

  @Override
  public AdventureSettings getEntity() {
    return settingsService.getSettings(AdventureSettings.class);
  }

  @Override
  public AdventureSettings updateEntity(AdventureSettings e) {
    AdventureSettings settings = settingsService.getSettings(AdventureSettings.class);

    settings.setJoinTimeout(e.getJoinTimeout());
    settings.setMinimumBet(e.getMinimumBet());
    settings.setMaximumBet(e.getMaximumBet());
    settings.setCooldown(e.getCooldown());
    settings.setWinMultiplier(e.getWinMultiplier());

    settingsService.save(settings);
    return settings;
  }
}
