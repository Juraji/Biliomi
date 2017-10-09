package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.components;

import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.components.tamagotchis.USTamagotchiToy;

import java.util.List;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USTamagotchis {
  private List<String> species;
  private List<USTamagotchiToy> toys;

  public List<String> getSpecies() {
    return species;
  }

  public void setSpecies(List<String> species) {
    this.species = species;
  }

  public List<USTamagotchiToy> getToys() {
    return toys;
  }

  public void setToys(List<USTamagotchiToy> toys) {
    this.toys = toys;
  }
}
