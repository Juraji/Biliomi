package nl.juraji.biliomi.rest.services.chat.settings;

import nl.juraji.biliomi.model.chat.ChatModeratorSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 17-6-2017.
 * Biliomi v3
 */
@Path("/chat/settings/chatmoderator")
public class ChatModeratorSettingsRestService extends SettingsModelRestService<ChatModeratorSettings> {

  @Override
  public ChatModeratorSettings getEntity() {
    return settingsService.getSettings(ChatModeratorSettings.class);
  }

  @Override
  public ChatModeratorSettings updateEntity(ChatModeratorSettings e) {
    ChatModeratorSettings settings = settingsService.getSettings(ChatModeratorSettings.class);

    settings.setLinksAllowed(e.isLinksAllowed());
    settings.setLinkPermitDuration(e.getLinkPermitDuration());
    settings.setExcessiveCapsAllowed(e.isExcessiveCapsAllowed());
    settings.setCapsTrigger(e.getCapsTrigger());
    settings.setCapsTriggerRatio(e.getCapsTriggerRatio());
    settings.setRepeatedCharactersAllowed(e.isRepeatedCharactersAllowed());
    settings.setRepeatedCharacterTrigger(e.getRepeatedCharacterTrigger());
    settings.setExemptedGroup(e.getExemptedGroup());
    settings.setFirstStrike(e.getFirstStrike());
    settings.setSecondStrike(e.getSecondStrike());
    settings.setThirdStrike(e.getThirdStrike());

    settings.getLinkWhitelist().clear();
    settings.getLinkWhitelist().addAll(e.getLinkWhitelist());

    settingsService.save(settings);
    return settings;
  }
}
