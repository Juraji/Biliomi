package nl.juraji.biliomi.rest.services.rest.games;

import nl.juraji.biliomi.model.games.RouletteSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/games/settings/roulette")
public class RouletteSettingsRestService extends SettingsModelRestService<RouletteSettings> {

  @Override
  public RouletteSettings getEntity() {
    return settingsService.getSettings(RouletteSettings.class);
  }

  @Override
  public RouletteSettings updateEntity(RouletteSettings e) {
    RouletteSettings settings = settingsService.getSettings(RouletteSettings.class);

    settings.setTimeoutOnDeathEnabled(e.isTimeoutOnDeathEnabled());
    settings.setTimeoutOnDeath(e.getTimeoutOnDeath());

    settingsService.save(settings);
    return settings;
  }
}
