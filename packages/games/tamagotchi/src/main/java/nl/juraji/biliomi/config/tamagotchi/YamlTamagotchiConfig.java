package nl.juraji.biliomi.config.tamagotchi;

import java.util.List;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
public class YamlTamagotchiConfig {
  private List<String> species;
  private List<YamlTamagotchiToy> toys;

  public List<String> getSpecies() {
    return species;
  }

  public void setSpecies(List<String> species) {
    this.species = species;
  }

  public List<YamlTamagotchiToy> getToys() {
    return toys;
  }

  public void setToys(List<YamlTamagotchiToy> toys) {
    this.toys = toys;
  }
}
