package nl.juraji.biliomi.rest.services.chat.settings;

import nl.juraji.biliomi.model.chat.AnnouncementsSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 17-6-2017.
 * Biliomi v3
 */
@Path("/chat/settings/announcements")
public class AnnoucementSettingsRestService extends SettingsModelRestService<AnnouncementsSettings> {

    @Override
    public AnnouncementsSettings getEntity() {
        return settingsService.getSettings(AnnouncementsSettings.class);
    }

    @Override
    public AnnouncementsSettings updateEntity(AnnouncementsSettings e) {
        AnnouncementsSettings settings = settingsService.getSettings(AnnouncementsSettings.class);

        settings.setEnabled(e.isEnabled());
        settings.setShuffle(e.isShuffle());
        settings.setRunInterval(e.getRunInterval());
        settings.setMinChatMessages(e.getMinChatMessages());

        settingsService.save(settings);
        return settings;
    }
}
