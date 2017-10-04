package nl.juraji.biliomi.rest.services.rest.core.settings;

import nl.juraji.biliomi.model.core.settings.PointsSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/core/settings/points")
public class PointsSettingsRestService extends SettingsModelRestService<PointsSettings> {

  @Override
  public PointsSettings getEntity() {
    return settingsService.getSettings(PointsSettings.class);
  }

  @Override
  public PointsSettings updateEntity(PointsSettings e) {
    PointsSettings settings = settingsService.getSettings(PointsSettings.class);

    settings.setPointsNameSingular(e.getPointsNameSingular());
    settings.setPointsNamePlural(e.getPointsNamePlural());
    settings.setTrackOnline(e.isTrackOnline());
    settings.setTrackOffline(e.isTrackOffline());
    settings.setOnlinePayoutInterval(e.getOnlinePayoutInterval());
    settings.setOfflinePayoutInterval(e.getOfflinePayoutInterval());
    settings.setOnlinePayoutAmount(e.getOnlinePayoutAmount());
    settings.setOfflinePayoutAmount(e.getOfflinePayoutAmount());

    settingsService.save(settings);
    return settings;
  }
}
