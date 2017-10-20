package nl.juraji.biliomi.components.games.tamagotchi.services;

import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.io.console.Ansi;
import nl.juraji.biliomi.model.games.Tamagotchi;
import nl.juraji.biliomi.model.games.TamagotchiToy;
import nl.juraji.biliomi.model.games.settings.TamagotchiSettings;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.BotName;
import nl.juraji.biliomi.utility.types.components.TimerService;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 29-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class AgingTimerService extends TimerService {

  @Inject
  private Logger logger;

  @Inject
  @BotName
  private String botName;

  @Inject
  private SettingsService settingsService;

  @Inject
  private TamagotchiService tamagotchiService;

  @Override
  public void start() {
    super.start();
    scheduleAtFixedRate(this::runAging, TamagotchiConstants.AGING_INTERVAL_HOURS, TimeUnit.HOURS);
  }

  public void runNow() {
    schedule(this::runAging, 0, TimeUnit.MILLISECONDS);
  }

  private void runAging() {
    List<Tamagotchi> tamagotchis = tamagotchiService.getAliveTamagotchis();

    tamagotchis.forEach(tamagotchi -> {
      // Expire toys
      tamagotchi.getToys().removeIf(toy -> toy.getExpiresAt().isBeforeNow());

      // If this tamagotchi belongs to the bot, do something different
      if (tamagotchi.getOwner().getUsername().equals(botName)) {
        ProcessBotTamagotchi(tamagotchi);
      } else {
        processPropDecay(tamagotchi);
      }
    });

    tamagotchiService.save(tamagotchis);
  }

  /**
   * Process property decay and toy modifiers
   *
   * @param tamagotchi The current tamagotchi
   */
  public void processPropDecay(Tamagotchi tamagotchi) {
    Set<TamagotchiToy> tamagotchiToys = tamagotchi.getToys();

    // Calculate toy modifiers
    double toyFoodMod = tamagotchiToys.stream().mapToDouble(toy -> TamagotchiConstants.PROPERTY_DECAY_PER_HOUR * toy.getFoodModifier()).sum();
    double toyMoodMod = tamagotchiToys.stream().mapToDouble(toy -> TamagotchiConstants.PROPERTY_DECAY_PER_HOUR * toy.getMoodModifier()).sum();
    double toyHygieneMod = tamagotchiToys.stream().mapToDouble(toy -> TamagotchiConstants.PROPERTY_DECAY_PER_HOUR * toy.getHygieneModifier()).sum();

    // Decay properties
    tamagotchi.setFoodStack(getDecayedValue(tamagotchi.getFoodStack(), toyFoodMod));
    tamagotchi.setMoodLevel(getDecayedValue(tamagotchi.getMoodLevel(), toyMoodMod));
    tamagotchi.setHygieneLevel(getDecayedValue(tamagotchi.getHygieneLevel(), toyHygieneMod));

    // Kill tamagotchi if food has gotten to 0
    if (tamagotchi.getFoodStack() <= 0) {
      // Remove any toy the tamagotchi had
      tamagotchi.getToys().clear();

      tamagotchiService.kill(tamagotchi);
      logger.info(Ansi.red("{} ran out of food and died"), tamagotchi.getName());
    }
  }

  /**
   * Process the bot's Tamagotchi, resetting properties to max.
   * Also if all toys have expired a new one will be randomly added, for fun.
   * In a way this is the bot taking care of it's own Tamagotchi
   *
   * @param tamagotchi The current (bot) tamagotchi
   */
  private void ProcessBotTamagotchi(Tamagotchi tamagotchi) {
    TamagotchiSettings settings = settingsService.getSettings(TamagotchiSettings.class);

    tamagotchi.setFoodStack(settings.getMaxFood());
    tamagotchi.setMoodLevel(settings.getMaxMood());
    tamagotchi.setHygieneLevel(settings.getMaxHygiene());
  }

  /**
   * Subtract PropertyDecayPerHour from the value clipping to 0.0
   *
   * @param value The value to decay
   * @return The decayed value
   */
  private double getDecayedValue(double value, double propModifier) {
    value -= (TamagotchiConstants.PROPERTY_DECAY_PER_HOUR - propModifier);
    return Double.max(value, 0);
  }
}
