package nl.juraji.biliomi.components.games.tamagotchi.services;

import nl.juraji.biliomi.model.internal.yaml.usersettings.UserSettings;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * Created by Juraji on 29-5-2017.
 * Biliomi v3
 */
@Default
public class SpeciesService {

  @Inject
  private UserSettings userSettings;

  private List<String> species;

  @PostConstruct
  private void initSpeciesService() {
    //noinspection unchecked
    species = userSettings.getBiliomi().getComponents().getTamagotchis().getSpecies();

    if (species == null || species.size() == 0) {
      throw new RuntimeException("No tamagotchi species defined, check the settings");
    }
  }

  /**
   * Compare the given species name with the available species
   *
   * @param speciesName The species name to compare
   * @return The matched species or null if no species matched
   */
  public String getSpeciesIfExists(String speciesName) {
    return species.stream()
        .filter(s -> s.equalsIgnoreCase(speciesName))
        .findFirst()
        .orElse(null);
  }

  /**
   * Get a list of species
   *
   * @return An immutable copy of the species list
   */
  public List<String> getAvailableSpecies() {
    return Collections.unmodifiableList(species);
  }
}
