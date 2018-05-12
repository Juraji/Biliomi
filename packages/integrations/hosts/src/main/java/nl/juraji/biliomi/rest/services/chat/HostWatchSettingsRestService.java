package nl.juraji.biliomi.rest.services.chat;

import nl.juraji.biliomi.model.chat.HostWatchSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/chat/settings/hostwatch")
public class HostWatchSettingsRestService extends SettingsModelRestService<HostWatchSettings> {

    @Override
    public HostWatchSettings getEntity() {
        return settingsService.getSettings(HostWatchSettings.class);
    }

    @Override
    public HostWatchSettings updateEntity(HostWatchSettings e) {
        HostWatchSettings settings = settingsService.getSettings(HostWatchSettings.class);

        settings.setReward(e.getReward());

        settingsService.save(settings);
        return settings;
    }
}
