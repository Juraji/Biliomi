package nl.juraji.biliomi.rest.services;

import nl.juraji.biliomi.components.integrations.steam.api.v1.SteamApi;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.model.integrations.SteamSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 * Created by Juraji on 21-1-2018.
 * Biliomi
 */
@Path("/social/steam/settings")
public class SteamSettingsRestService extends SettingsModelRestService<SteamSettings> {

  @Inject
  private SteamApi steamApi;

  @Inject
  private SettingsService settingsService;

  @Override
  public SteamSettings getEntity() {
    SteamSettings settings = settingsService.getSettings(SteamSettings.class);

    // Set integration enabled process variable
    settings.set_integrationEnabled(steamApi.isAvailable());

    return settings;
  }

  @Override
  public SteamSettings updateEntity(SteamSettings e) {
    SteamSettings settings = settingsService.getSettings(SteamSettings.class);

    settings.setAutoUpdateChannelGame(e.isAutoUpdateChannelGame());

    settingsService.save(settings);
    return settings;
  }
}
