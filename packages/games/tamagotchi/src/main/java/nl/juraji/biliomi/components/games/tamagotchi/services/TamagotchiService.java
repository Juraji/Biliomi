package nl.juraji.biliomi.components.games.tamagotchi.services;

import nl.juraji.biliomi.components.games.tamagotchi.TamagotchiComponent;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.Tamagotchi;
import nl.juraji.biliomi.model.games.TamagotchiDao;
import nl.juraji.biliomi.model.games.settings.TamagotchiSettings;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.cdi.annotations.modifiers.I18nData;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.collections.I18nMap;
import org.joda.time.DateTime;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Juraji on 28-5-2017.
 * Biliomi v3
 */
@Default
public class TamagotchiService {

  @Inject
  @I18nData(TamagotchiComponent.class)
  private I18nMap i18n;

  @Inject
  private SettingsService settingsService;

  @Inject
  private GenderService genderService;

  @Inject
  private TamagotchiDao tamagotchiDao;

  private TamagotchiSettings settings;

  @PostConstruct
  private void initTamagotchiService() {
    settings = settingsService.getSettings(TamagotchiSettings.class, s -> settings = s);
  }

  /**
   * Create a new Tamagotchi (Expects input to have been checked)
   *
   * @param name    The new tamagotchi's name
   * @param owner   The owner User
   * @param species The preferred species (Is set without checking)
   * @return The new Tamagotchi or null if the preferred species does not exist
   */
  public Tamagotchi createTamagotchi(String name, User owner, String species) {
    Tamagotchi tamagotchi = new Tamagotchi();

    tamagotchi.setName(name);
    tamagotchi.setOwner(owner);
    tamagotchi.setSpecies(species);
    tamagotchi.setGender(genderService.getRandom());
    tamagotchi.setFoodStack(settings.getMaxFood());
    tamagotchi.setMoodLevel(settings.getMaxMood());
    tamagotchi.setHygieneLevel(settings.getMaxHygiene());
    tamagotchi.setAffection(1);
    tamagotchi.setDateOfBirth(DateTime.now());

    tamagotchiDao.save(tamagotchi);
    return tamagotchi;
  }

  /**
   * Get the proper gender name
   *
   * @param tamagotchi The Tamagotchi
   * @return The gender name
   */
  public String getGenderName(Tamagotchi tamagotchi) {
    return genderService.getGenderName(tamagotchi.getGender());
  }

  /**
   * Get the proper gender address
   *
   * @param tamagotchi The Tamagotchi
   * @return The gender address
   */
  public String getGenderAddress(Tamagotchi tamagotchi) {
    return genderService.getAddress(tamagotchi.getGender());
  }

  /**
   * Get the proper gender referal
   *
   * @param tamagotchi The Tamagotchi
   * @return The gender referal
   */
  public String getGenderReferal(Tamagotchi tamagotchi) {
    return genderService.getReferal(tamagotchi.getGender());
  }

  /**
   * Get the proper gender possesive referal
   *
   * @param tamagotchi The Tamagotchi
   * @return The gender referal
   */
  public String getGenderPosessiveReferal(Tamagotchi tamagotchi) {
    return genderService.getPosessiveReferal(tamagotchi.getGender());
  }

  public String getMoodText(Tamagotchi tamagotchi) {
    double moodPercentage = MathUtils.calcPercentage(tamagotchi.getMoodLevel(), settings.getMaxMood());
    Templater templater;

    if (moodPercentage < TamagotchiConstants.MOOD_SAD_THRESHOLD) {
      templater = i18n.get("Common.tamagotchi.mood.sad");
    } else if (moodPercentage < TamagotchiConstants.MOOD_BORED_THRESHOLD) {
      templater = i18n.get("Common.tamagotchi.mood.bored");
    } else {
      templater = i18n.get("Common.tamagotchi.mood.happy");
    }

    return templater.apply();
  }

  public String getHygieneState(Tamagotchi tamagotchi) {
    double hygienePercentage = MathUtils.calcPercentage(tamagotchi.getHygieneLevel(), settings.getMaxHygiene());
    Templater templater;

    if (hygienePercentage < TamagotchiConstants.MOOD_SAD_THRESHOLD) {
      templater = i18n.get("Common.tamagotchi.hygieneState.filthy");
    } else if (hygienePercentage < TamagotchiConstants.MOOD_BORED_THRESHOLD) {
      templater = i18n.get("Common.tamagotchi.hygieneState.aBitDirty");
    } else {
      templater = i18n.get("Common.tamagotchi.hygieneState.clean");
    }

    return templater.apply();
  }

  /**
   * Check if the given User owns an alive tamagotchi
   *
   * @param user The owner to check
   * @return True if a tamagotchi exists, else False
   */
  public boolean userHasTamagotchi(User user) {
    return tamagotchiDao.userHasTamagotchi(user);
  }

  /**
   * Get a User's current alive tamagotchi
   *
   * @param user The owner of the wanted tamagotchi
   * @return The Tamagotchi linked to the user, or null of none exist
   */
  public Tamagotchi getTamagotchi(User user) {
    return tamagotchiDao.getByUser(user);
  }

  /**
   * Get a list of currently alive tamagotchis
   *
   * @return A List of Tamagotchi
   */
  public List<Tamagotchi> getAliveTamagotchis() {
    return tamagotchiDao.getAliveTamagotchis();
  }

  /**
   * Find out if a Tamagotchi has enough food, to have the given percentage taken off
   * @param tamagotchi The Tamagotchi to check
   * @param percentage The percentage to check
   * @return True if the Tamagotchi has sufficient food, else False
   */
  public boolean hasNotEnoughFood(Tamagotchi tamagotchi, double percentage) {
    double amount = settings.getMaxFood() * percentage;
    return tamagotchi.getFoodStack() <= amount;
  }

  /**
   * Add food to a tamagotchi
   * Deceased tamagotchis will do nothing
   * Negative amounts will be summed with any toy modifiers
   *
   * @param tamagotchi The tamagotchi to manage
   * @param percentage The percentage of food to give (-1...1), use a negative value to take food
   */
  public void addToFoodStack(Tamagotchi tamagotchi, double percentage) {
    if (tamagotchi.isDeceased()) {
      return;
    }

    double amount = settings.getMaxFood() * percentage;

    if (amount < 0) {
      // If amount is subtraction correct it with any toys modifying this property
      final double finalAmount = Math.abs(amount);
      amount += tamagotchi.getToys().stream()
          .mapToDouble(tamagotchiToy -> finalAmount * tamagotchiToy.getFoodModifier())
          .sum();
    }

    double v = MathUtils.minMax(tamagotchi.getFoodStack() + amount, 0, settings.getMaxFood());
    tamagotchi.setFoodStack(v);
  }

  /**
   * Add to the mood to a tamagotchi
   * Deceased tamagotchis will do nothing
   * Negative amounts will be summed with any toy modifiers
   *
   * @param tamagotchi The tamagotchi to manage
   * @param percentage The percentage of mood to add (-1...1), use a negative value to lower mood
   */
  public void addToMoodLevel(Tamagotchi tamagotchi, double percentage) {
    if (tamagotchi.isDeceased()) {
      return;
    }
    double amount = settings.getMaxMood() * percentage;

    if (amount < 0) {
      // If amount is subtraction correct it with any toys modifying this property
      final double finalAmount = Math.abs(amount);
      amount += tamagotchi.getToys().stream()
          .mapToDouble(tamagotchiToy -> finalAmount * tamagotchiToy.getMoodModifier())
          .sum();
    }

    double v = MathUtils.minMax(tamagotchi.getMoodLevel() + amount, 0, settings.getMaxMood());
    tamagotchi.setMoodLevel(v);
  }

  /**
   * Add to the hygiene to a tamagotchi
   * Deceased tamagotchis will do nothing
   * Negative amounts will be summed with any toy modifiers
   *
   * @param tamagotchi The tamagotchi to manage
   * @param percentage The percentage of hygiene to add (-1...1), use a negative value to lower hygiene
   */
  public void addToHygieneLevel(Tamagotchi tamagotchi, double percentage) {
    if (tamagotchi.isDeceased()) {
      return;
    }
    double amount = settings.getMaxHygiene() * percentage;

    if (amount < 0) {
      // If amount is subtraction correct it with any toys modifying this property
      final double finalAmount = Math.abs(amount);
      amount += tamagotchi.getToys().stream()
          .mapToDouble(tamagotchiToy -> finalAmount * tamagotchiToy.getHygieneModifier())
          .sum();
    }

    double v = MathUtils.minMax(tamagotchi.getHygieneLevel() + amount, 0, settings.getMaxHygiene());
    tamagotchi.setHygieneLevel(v);
  }

  /**
   * Add a point to the Tamagotchi's affection
   *
   * @param tamagotchi The tamagotchi to manage
   */
  public void increaseAffection(Tamagotchi tamagotchi) {
    if (tamagotchi.isDeceased()) {
      return;
    }
    tamagotchi.setAffection(tamagotchi.getAffection() + 1);
  }

  /**
   * Kill a tamagotchi
   *
   * @param tamagotchi The tamagotchi to kill
   */
  public void kill(Tamagotchi tamagotchi) {
    tamagotchi.setDeceased(true);
    tamagotchi.setDateOfDeath(DateTime.now());
  }

  /**
   * Save changes to a tamagotchi
   *
   * @param tamagotchis The tamagotchi to save
   */
  public void save(Tamagotchi... tamagotchis) {
    tamagotchiDao.save(Arrays.asList(tamagotchis));
  }

  /**
   * Save changes to a collection of tamagotchis
   *
   * @param tamagotchis The collection of tamagotchis to save
   */
  public void save(Collection<Tamagotchi> tamagotchis) {
    tamagotchiDao.save(tamagotchis);
  }
}
