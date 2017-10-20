package nl.juraji.biliomi.rest.services.chat.settings;

import nl.juraji.biliomi.model.subscribers.SubscriberWatchSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 8-9-2017.
 * Biliomi v3
 */
@Path("/chat/settings/subscriberwatch")
public class SubscriberWatchSettingsRestService extends SettingsModelRestService<SubscriberWatchSettings> {

  @Override
  public SubscriberWatchSettings getEntity() {
    return settingsService.getSettings(SubscriberWatchSettings.class);
  }

  @Override
  public SubscriberWatchSettings updateEntity(SubscriberWatchSettings e) {
    SubscriberWatchSettings settings = settingsService.getSettings(SubscriberWatchSettings.class);

    settings.setRewardTier1(e.getRewardTier1());
    settings.setRewardTier2(e.getRewardTier2());
    settings.setRewardTier3(e.getRewardTier3());

    settingsService.save(settings);
    return settings;
  }
}
