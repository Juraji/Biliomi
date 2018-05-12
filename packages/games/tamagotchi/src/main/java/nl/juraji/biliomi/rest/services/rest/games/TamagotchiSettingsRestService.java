package nl.juraji.biliomi.rest.services.rest.games;

import nl.juraji.biliomi.model.games.settings.TamagotchiSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/games/settings/tamagotchi")
public class TamagotchiSettingsRestService extends SettingsModelRestService<TamagotchiSettings> {

    @Override
    public TamagotchiSettings getEntity() {
        return settingsService.getSettings(TamagotchiSettings.class);
    }

    @Override
    public TamagotchiSettings updateEntity(TamagotchiSettings e) {
        TamagotchiSettings settings = settingsService.getSettings(TamagotchiSettings.class);

        settings.setNewPrice(e.getNewPrice());
        settings.setFoodPrice(e.getFoodPrice());
        settings.setSoapPrice(e.getSoapPrice());
        settings.setMaxFood(e.getMaxFood());
        settings.setMaxMood(e.getMaxMood());
        settings.setMaxHygiene(e.getMaxHygiene());
        settings.setNameMaxLength(e.getNameMaxLength());
        settings.setBotTamagotchiEnabled(e.isBotTamagotchiEnabled());

        settingsService.save(settings);
        return settings;
    }
}
