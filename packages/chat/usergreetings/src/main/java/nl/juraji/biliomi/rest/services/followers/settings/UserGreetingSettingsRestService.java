package nl.juraji.biliomi.rest.services.followers.settings;

import nl.juraji.biliomi.model.chat.UserGreetingSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 17-6-2017.
 * Biliomi v3
 */
@Path("/chat/settings/usergreetings")
public class UserGreetingSettingsRestService extends SettingsModelRestService<UserGreetingSettings> {

    @Override
    public UserGreetingSettings getEntity() {
        return settingsService.getSettings(UserGreetingSettings.class);
    }

    @Override
    public UserGreetingSettings updateEntity(UserGreetingSettings e) {
        UserGreetingSettings settings = settingsService.getSettings(UserGreetingSettings.class);

        settings.setEnableGreetings(e.isEnableGreetings());
        settings.setGreetingTimeout(e.getGreetingTimeout());

        settingsService.save(settings);
        return settings;
    }
}
