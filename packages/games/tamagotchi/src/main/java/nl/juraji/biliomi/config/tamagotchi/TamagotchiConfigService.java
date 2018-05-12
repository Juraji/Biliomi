package nl.juraji.biliomi.config.tamagotchi;

import nl.juraji.biliomi.config.ConfigService;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
@Default
@Singleton
public class TamagotchiConfigService extends ConfigService<YamlTamagotchiConfig> {

    public TamagotchiConfigService() {
        super("games/tamagotchis.yml", YamlTamagotchiConfig.class);
    }

    public List<YamlTamagotchiToy> getToys() {
        return config.getToys();
    }

    public List<String> getAvailableSpecies() {
        return config.getSpecies();
    }

    public String getSpeciesIfExists(String input) {
        return config.getSpecies().stream()
                .filter(species -> species.equalsIgnoreCase(input))
                .findFirst()
                .orElse(null);
    }
}
