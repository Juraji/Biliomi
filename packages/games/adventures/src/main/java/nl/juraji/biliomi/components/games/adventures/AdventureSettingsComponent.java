package nl.juraji.biliomi.components.games.adventures;

import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.AdventureSettings;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 5-6-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class AdventureSettingsComponent extends Component {

  private AdventureSettings settings;

  @Inject
  private TimeFormatter timeFormatter;

  @Inject
  private PointsService pointsService;

  @Override
  public void init() {
    settings = settingsService.getSettings(AdventureSettings.class, s -> settings = s);
  }

  /**
   * Manage adventure settings
   * Usage: !adventuresettings [jointimeout|minimumbet|maximumbet|cooldown|winmultiplier] [more...]
   */
  @CommandRoute(command = "adventuresettings", systemCommand = true)
  public boolean adventuresettingsCommand(User user, Arguments arguments) {
    return captureSubCommands("adventuresettings", i18n.supply("ChatCommand.adventuresettings.usage"), user, arguments);
  }

  /**
   * Set the timout for joining adventures, after an adventure has been initiated
   * Usage: !adventuresettings jointimeout [minutes 1...10]
   */
  @SubCommandRoute(parentCommand = "adventuresettings", command = "jointimeout")
  public boolean adventuresettingsCommandJoinTimeout(User user, Arguments arguments) {
    Integer minutes = Numbers.asNumber(arguments.get(0)).toInteger();

    if (minutes == null || minutes < 1 || minutes > 10) {
      chat.whisper(user, i18n.get("ChatCommand.adventuresettings.jointimeout.usage"));
      return false;
    }

    long miliseconds = TimeUnit.MILLISECONDS.convert(minutes, TimeUnit.MINUTES);
    settings.setJoinTimeout(miliseconds);
    settingsService.save(settings);

    chat.whisper(user, i18n.get("ChatCommand.adventuresettings.jointimeout.saved")
        .add("time", () -> timeFormatter.timeQuantity(miliseconds, TimeUnit.MINUTES)));
    return true;
  }

  /**
   * Set the minimum bet for initiating/joining an adventure
   * Usage: !adventuresettings minimumbet [amount of points]
   */
  @SubCommandRoute(parentCommand = "adventuresettings", command = "minimumbet")
  public boolean adventuresettingsCommandMinimumBet(User user, Arguments arguments) {
    Long minPoints = Numbers.asNumber(arguments.get(0)).toLong();

    if (minPoints == null || minPoints < 1) {
      chat.whisper(user, i18n.get("ChatCommand.adventuresettings.minimumbet.usage"));
      return false;
    }

    settings.setMinimumBet(minPoints);
    settingsService.save(settings);

    chat.whisper(user, i18n.get("ChatCommand.adventuresettings.minimumbet.saved")
        .add("points", () -> pointsService.asString(minPoints)));
    return true;
  }

  /**
   * Set the maximum bet for initiating/joining an adventure
   * Usage: !adventuresettings minimumbet [amount of points]
   */
  @SubCommandRoute(parentCommand = "adventuresettings", command = "maximumbet")
  public boolean adventuresettingsCommandMaximumBet(User user, Arguments arguments) {
    Long maxPoints = Numbers.asNumber(arguments.get(0)).toLong();

    if (maxPoints == null || maxPoints < 1) {
      chat.whisper(user, i18n.get("ChatCommand.adventuresettings.maximumbet.usage"));
      return false;
    }

    settings.setMaximumBet(maxPoints);
    settingsService.save(settings);

    chat.whisper(user, i18n.get("ChatCommand.adventuresettings.maximumbet.saved")
        .add("points", () -> pointsService.asString(maxPoints)));
    return true;
  }

  /**
   * Set the cooldown for the next adventure
   * Usage: !adventuresettings cooldown [minutes]
   */
  @SubCommandRoute(parentCommand = "adventuresettings", command = "cooldown")
  public boolean adventuresettingsCommandCooldown(User user, Arguments arguments) {
    Integer cooldownMinutes = Numbers.asNumber(arguments.get(0)).toInteger();

    if (cooldownMinutes == null || cooldownMinutes < 0) {
      chat.whisper(user, i18n.get("ChatCommand.adventuresettings.cooldown.usage"));
      return false;
    }

    long cooldownMillis = TimeUnit.MICROSECONDS.convert(cooldownMinutes, TimeUnit.MINUTES);
    settings.setCooldown(cooldownMillis);
    settingsService.save(settings);

    chat.whisper(user, i18n.get("ChatCommand.adventuresettings.cooldown.saved")
        .add("time", () -> timeFormatter.timeQuantity(cooldownMillis)));
    return true;
  }

  /**
   * Set the bet multiplier for survivors. The survivors bet will be multiplied by this value and given to the survivor, along with the original bet
   * Usage: !adventuresettings multiplier [0...1]
   */
  @SubCommandRoute(parentCommand = "adventuresettings", command = "multiplier")
  public boolean adventuresettingsCommandWinMultiplier(User user, Arguments arguments) {
    Double multiplier = Numbers.asNumber(arguments.get(0)).toDouble();

    if (multiplier == null || multiplier < 0 || multiplier > 1) {
      chat.whisper(user, i18n.get("ChatCommand.adventuresettings.multiplier.usage"));
      return false;
    }

    settings.setWinMultiplier(multiplier);
    settingsService.save(settings);

    chat.whisper(user, i18n.get("ChatCommand.adventuresettings.multiplier.saved")
        .add("pointsname", pointsService::pointsName)
        .add("percentage", () -> MathUtils.doubleToPercentage(multiplier)));
    return true;
  }
}
