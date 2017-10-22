package nl.juraji.biliomi.components.games.adventures;

import nl.juraji.biliomi.components.games.adventures.services.AdventureRecordService;
import nl.juraji.biliomi.components.games.adventures.services.AdventureRunnerService;
import nl.juraji.biliomi.components.games.adventures.services.AdventureState;
import nl.juraji.biliomi.components.games.tamagotchi.services.TamagotchiConstants;
import nl.juraji.biliomi.components.games.tamagotchi.services.TamagotchiService;
import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.AdventureSettings;
import nl.juraji.biliomi.model.games.Tamagotchi;
import nl.juraji.biliomi.model.games.UserAdventureRecordStats;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

import static nl.juraji.biliomi.components.games.adventures.services.AdventureState.*;

/**
 * Created by Juraji on 5-6-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class AdventureComponent extends Component {
  private static final int TG_MIN_AFFECTION = 10;
  private static final double TG_MIN_MOOD = TamagotchiConstants.MOOD_SAD_THRESHOLD;
  private static final double TG_FOOD_DECREASE = -0.1;
  private static final double TG_MOOD_INCREASE = 0.1;
  private static final double TG_HYGIENE_DECREASE = -0.5;

  @Inject
  private AdventureRunnerService adventureRunnerService;

  @Inject
  private PointsService pointsService;

  @Inject
  private TamagotchiService tamagotchiService;

  @Inject
  private AdventureRecordService adventureRecordService;

  @Inject
  private TimeFormatter timeFormatter;
  private AdventureSettings settings;

  @Override
  public void init() {
    settings = settingsService.getSettings(AdventureSettings.class, s -> settings = s);
  }

  /**
   * Initiate or join an adventure
   * Usage: !adventure [amount of points]
   */
  @CommandRoute(command = "adventure")
  public boolean adventureCommand(User user, Arguments arguments) {
    Long betPoints = Numbers.asNumber(arguments.get(0)).toLong();

    if (betPoints == null) {
      chat.whisper(user, i18n.get("ChatCommand.adventure.usage"));
      return false;
    }

    AdventureState adventureState = adventureRunnerService.getState();

    // Check if the adventure is not already running
    if (RUNNING.equals(adventureState)) {
      chat.whisper(user, i18n.get("ChatCommand.adventure.inProgress"));
      return false;
    }

    if (NOT_RUNNING.equals(adventureState)) {
      // Check adventure cooldown
      DateTime nextRun = adventureRunnerService.getNextRun();
      DateTime now = DateTime.now();
      if (now.isBefore(nextRun)) {
        chat.whisper(user, i18n.get("ChatCommand.adventure.cooldownActive")
            .add("time", () -> timeFormatter.timeQuantityUntil(nextRun)));
        return false;
      }
    }

    // Check if user is not already in adventure
    if (adventureRunnerService.userExists(user)) {
      chat.whisper(user, i18n.get("ChatCommand.adventure.alreadyJoined"));
      return false;
    }

    // Check if user has the dosh to bet
    if (user.getPoints() < betPoints) {
      chat.whisper(user, i18n.get("ChatCommand.adventure.noCredit")
          .add("betpoints", () -> pointsService.asString(betPoints))
          .add("balance", () -> pointsService.asString(user.getPoints())));
      return false;
    }

    // Bet is above minimum
    if (betPoints < settings.getMinimumBet()) {
      chat.whisper(user, i18n.get("ChatCommand.adventure.betTooLow")
          .add("betpoints", () -> pointsService.asString(betPoints))
          .add("points", () -> pointsService.asString(settings.getMinimumBet())));
      return false;
    }

    // Bet is below maximum
    if (betPoints > settings.getMaximumBet()) {
      chat.whisper(user, i18n.get("ChatCommand.adventure.betTooHigh")
          .add("betpoints", () -> pointsService.asString(betPoints))
          .add("points", () -> pointsService.asString(settings.getMaximumBet())));
      return false;
    }

    pointsService.take(user, betPoints);
    Tamagotchi tamagotchi = tamagotchiService.getTamagotchi(user);
    String langKey;
    if (tamagotchi != null && tamagotchi.getAffection() > TG_MIN_AFFECTION && tamagotchi.getMoodLevel() > TG_MIN_MOOD) {
      tamagotchiService.addToFoodStack(tamagotchi, TG_FOOD_DECREASE);
      tamagotchiService.addToMoodLevel(tamagotchi, TG_MOOD_INCREASE);
      tamagotchiService.addToHygieneLevel(tamagotchi, TG_HYGIENE_DECREASE);
      tamagotchiService.save(tamagotchi);

      adventureRunnerService.join(user, tamagotchi, betPoints);
      langKey = "ChatCommand.adventure.joinedWithTamagotchi";
    } else {
      adventureRunnerService.join(user, null, betPoints);
      langKey = "ChatCommand.adventure.joined";
    }

    if (NOT_RUNNING.equals(adventureState)) {
      chat.say(i18n.get("ChatCommand.adventure.newAdventureStarted")
          .add("username", user::getNameAndTitle));
    } else if (JOIN.equals(adventureState)) {
      chat.say(i18n.get(langKey)
          .add("username", user::getNameAndTitle)
          .add("tamagotchiname", () -> tamagotchi != null ? tamagotchi.getName() : ""));
    }

    return true;
  }

  /**
   * Have biliomi calculate and state your adventure statistics
   * Usage: !myadventurerecord
   */
  @CommandRoute(command = "myadventurerecord")
  public boolean myadventurerecordCommand(User user, Arguments arguments) {
    UserAdventureRecordStats recordInfo = adventureRecordService.getRecordInfo(user);

    if (recordInfo == null) {
      chat.say(i18n.get("ChatCommand.myadventurerecord.noRecords")
          .add("username", user::getDisplayName));
      return true;
    }

    chat.say(i18n.get("ChatCommand.myadventurerecord.stats")
        .add("username", user::getDisplayName)
        .add("total", recordInfo::getRecordCount)
        .add("survivdecount", recordInfo::getWins)
        .add("totalpoints", () -> pointsService.asString(recordInfo.getTotalPayout())));

    if (recordInfo.getPercentageByTamagotchi() > 0) {
      chat.say(i18n.get("ChatCommand.myadventurerecord.stats.byTamagotchi")
          .add("username", user::getDisplayName)
          .add("percentage", () -> MathUtils.doubleToPercentage(recordInfo.getPercentageByTamagotchi())));
    }

    return true;
  }
}
