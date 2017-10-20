package nl.juraji.biliomi.rest.services.chat.settings;

import nl.juraji.biliomi.model.chat.BitsSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 11-9-2017.
 * Biliomi v3
 */
@Path("/chat/settings/bits")
public class BitsSettingsRestService extends SettingsModelRestService<BitsSettings> {

  @Override
  public BitsSettings getEntity() {
    return settingsService.getSettings(BitsSettings.class);
  }

  @Override
  public BitsSettings updateEntity(BitsSettings e) {
    BitsSettings settings = settingsService.getSettings(BitsSettings.class);

    settings.setEnableBitsToPoints(e.isEnableBitsToPoints());
    settings.setBitsToPointsPayoutToAllChatters(e.isBitsToPointsPayoutToAllChatters());
    settings.setBitsToPointsMultiplier(e.getBitsToPointsMultiplier());

    settingsService.save(settings);
    return settings;
  }
}
