package nl.juraji.biliomi.components.games.tamagotchi.services;

import nl.juraji.biliomi.model.games.Tamagotchi;
import nl.juraji.biliomi.model.games.TamagotchiToy;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.exceptions.SettingsDefinitionException;
import nl.juraji.biliomi.utility.settings.AppSettingProvider;
import nl.juraji.biliomi.utility.settings.UserSettings;
import nl.juraji.biliomi.utility.types.collections.CIMap;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 31-5-2017.
 * Biliomi v3
 */
@Default
public class ToyFactoryService {
  private final CIMap<Map<String, Object>> toyData = new CIMap<>();

  @Inject
  private UserSettings userSettings;

  @PostConstruct
  private void initToyFactoryService() {
    //noinspection unchecked
    List<Map<String, Object>> toyTemplates = (List<Map<String, Object>>) userSettings.getObjectValue("biliomi.component.tamagotchis.toys");

    if (toyTemplates == null || toyTemplates.size() == 0) {
      throw new SettingsDefinitionException("No toys defined, check the settings");
    }

    boolean incorrectToyData = toyTemplates.stream().anyMatch(toyDefinition ->
        AppSettingProvider.isInvalidMapProperty("name", String.class, toyDefinition)
            || AppSettingProvider.isInvalidMapProperty("durationDays", Integer.class, toyDefinition)
            || AppSettingProvider.isInvalidMapProperty("foodModifier", Double.class, toyDefinition)
            || AppSettingProvider.isInvalidMapProperty("moodModifier", Double.class, toyDefinition)
            || AppSettingProvider.isInvalidMapProperty("hygieneModifier", Double.class, toyDefinition)
            || AppSettingProvider.isInvalidMapProperty("cost", Integer.class, toyDefinition));
    if (incorrectToyData) {
      throw new RuntimeException("Tamagotchi toys defined incorrectly, check the settings");
    }
    EStream.from(toyTemplates)
        .mapToBiEStream(r -> (String) r.get("name"), r -> r)
        .forEach(this.toyData::put);
  }

  public Set<String> getToyNameSet() {
    return Collections.unmodifiableSet(toyData.keySet());
  }

  public TamagotchiToy getToy(String toyName) {
    Map<String, Object> toyDefinition = toyData.get(toyName);

    if (toyDefinition == null) {
      return null;
    }

    return generateToy(toyDefinition);
  }

  public List<TamagotchiToy> getList() {
    return toyData.values().stream()
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

  private static TamagotchiToy generateToy(Map<String, Object> toyDefinition) {
    long daysInMillis = TimeUnit.MILLISECONDS.convert((int) toyDefinition.get("durationDays"), TimeUnit.DAYS);
    DateTime expiryDate = DateTime.now().plus(Duration.millis(daysInMillis));
    TamagotchiToy toy = new TamagotchiToy();

    toy.setToyName((String) toyDefinition.get("name"));
    toy.setExpiresAt(expiryDate);
    toy.setFoodModifier((double) toyDefinition.get("foodModifier"));
    toy.setMoodModifier((double) toyDefinition.get("moodModifier"));
    toy.setHygieneModifier((double) toyDefinition.get("hygieneModifier"));
    toy.setCost((int) toyDefinition.get("cost"));

    return toy;
  }
}
