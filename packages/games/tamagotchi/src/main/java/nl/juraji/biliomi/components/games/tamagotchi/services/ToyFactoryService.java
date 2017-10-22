package nl.juraji.biliomi.components.games.tamagotchi.services;

import nl.juraji.biliomi.config.tamagotchi.TamagotchiConfigService;
import nl.juraji.biliomi.config.tamagotchi.YamlTamagotchiToy;
import nl.juraji.biliomi.model.games.Tamagotchi;
import nl.juraji.biliomi.model.games.TamagotchiToy;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 31-5-2017.
 * Biliomi v3
 */
@Default
public class ToyFactoryService {

  @Inject
  private TamagotchiConfigService configService;

  public Set<String> getToyNameSet() {
    return configService.getToys().stream()
        .map(YamlTamagotchiToy::getName)
        .collect(Collectors.toSet());
  }

  public TamagotchiToy getToy(String toyName) {
    YamlTamagotchiToy toyDef = configService.getToys().stream()
        .filter(YamlTamagotchiToy -> YamlTamagotchiToy.getName().equalsIgnoreCase(toyName))
        .findFirst()
        .orElse(null);

    if (toyDef == null) {
      return null;
    }

    return generateToy(toyDef);
  }

  public List<TamagotchiToy> getList() {
    return configService.getToys().stream()
        .map(ToyFactoryService::generateToy)
        .collect(Collectors.toList());
  }

  public TamagotchiToy getRandom() {
    return MathUtils.listRand(getList());
  }

  public boolean tamagotchiHasToy(Tamagotchi tamagotchi, TamagotchiToy toy) {
    return tamagotchi.getToys().stream()
        .anyMatch(tamagotchiToy -> tamagotchiToy.getToyName().equals(toy.getToyName()));
  }

  private static TamagotchiToy generateToy(YamlTamagotchiToy toyDef) {
    long daysInMillis = TimeUnit.MILLISECONDS.convert(toyDef.getDurationDays(), TimeUnit.DAYS);
    DateTime expiryDate = DateTime.now().plus(Duration.millis(daysInMillis));
    TamagotchiToy toy = new TamagotchiToy();

    toy.setToyName(toyDef.getName());
    toy.setExpiresAt(expiryDate);
    toy.setFoodModifier(toyDef.getFoodModifier());
    toy.setMoodModifier(toyDef.getMoodModifier());
    toy.setHygieneModifier(toyDef.getHygieneModifier());
    toy.setCost(toyDef.getCost());

    return toy;
  }
}
