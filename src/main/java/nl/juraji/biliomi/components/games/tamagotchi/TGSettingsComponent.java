package nl.juraji.biliomi.components.games.tamagotchi;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.settings.TamagotchiSettings;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import org.joda.time.Duration;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

import static nl.juraji.biliomi.components.games.tamagotchi.services.TamagotchiConstants.*;

/**
 * Created by Juraji on 29-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class TGSettingsComponent extends Component {

  @Inject
  private PointsService pointsService;

  @Inject
  private TimeFormatter timeFormatter;

  private TamagotchiSettings settings;

  @Override
  public void init() {
    super.init();
    settings = settingsService.getSettings(TamagotchiSettings.class, s -> settings = s);
  }

  /**
   * Manage Tamagotchi settings
   * Usage: !tamagotchisettings [newprice|foodprice|maxfood|maxmood|maxhygiene|namemaxlength] [more...]
   */
  @CommandRoute(command = "tamagotchisettings", systemCommand = true)
  public boolean tamagotchisettingsCommand(User user, Arguments arguments) {
    return captureSubCommands("tamagotchisettings", l10n.supply("ChatCommand.tamagotchisettings.usage"), user, arguments);
  }

  /**
   * Set the price for buying a new Tamagotchi
   * Usage: !tamagotchisettings newprice [amount of points]
   */
  @SubCommandRoute(parentCommand = "tamagotchisettings", command = "newprice")
  public boolean tamagotchisettingsCommandNewPrice(User user, Arguments arguments) {
    Long points = Numbers.asNumber(arguments.get(0)).toLong();

    if (points == null || points < 0) {
      chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.newprice.usage"));
      return false;
    }

    settings.setNewPrice(points);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.newprice.updated")
        .add("points", pointsService.asString(points)));
    return true;
  }

  /**
   * Set the price for a single piece of food
   * Usage: !tamagotchisettings foodprice [amount of points]
   */
  @SubCommandRoute(parentCommand = "tamagotchisettings", command = "foodprice")
  public boolean tamagotchisettingsCommandFoodPrice(User user, Arguments arguments) {
    Long points = Numbers.asNumber(arguments.get(0)).toLong();

    if (points == null || points < 0) {
      chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.foodprice.usage"));
      return false;
    }

    settings.setFoodPrice(points);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.foodprice.updated")
        .add("points", pointsService.asString(points)));
    return true;
  }

  /**
   * Set the price for a cleanup of a Tamagotchi
   * Usage: !tamagotchisettings soapprice [amount of points]
   */
  @SubCommandRoute(parentCommand = "tamagotchisettings", command = "soapprice")
  public boolean tamagotchisettingsCommandSoapPrice(User user, Arguments arguments) {
    Long points = Numbers.asNumber(arguments.get(0)).toLong();

    if (points == null || points < 0) {
      chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.soapprice.usage"));
      return false;
    }

    settings.setSoapPrice(points);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.soapprice.updated")
        .add("points", pointsService.asString(points)));
    return true;
  }

  /**
   * Set the maximum of food a Tamagotchi can have at any time
   * Note: This value translates to days before ful decay
   * Usage: !tamagotchisettings maxfood [amount]
   */
  @SubCommandRoute(parentCommand = "tamagotchisettings", command = "maxfood")
  public boolean tamagotchisettingsCommandMaxFood(User user, Arguments arguments) {
    Double max = Numbers.asNumber(arguments.get(0)).toDouble();

    if (max == null || max < 0) {
      chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.maxfood.usage"));
      return false;
    }

    settings.setMaxFood(max);
    settingsService.save(settings);

    int maxAbs = max.intValue();
    chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.maxfood.updated")
        .add("max", maxAbs)
        .add("ttl", this.getTimeToLiveString(maxAbs)));
    return true;
  }

  /**
   * Set the maximum mood level a Tamagotchi can achieve
   * Note: This value translates to days before ful decay
   * Usage: !tamagotchisettings maxmood [amount]
   */
  @SubCommandRoute(parentCommand = "tamagotchisettings", command = "maxmood")
  public boolean tamagotchisettingsCommandMaxMood(User user, Arguments arguments) {
    Double max = Numbers.asNumber(arguments.get(0)).toDouble();

    if (max == null || max < 0) {
      chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.maxmood.usage"));
      return false;
    }

    settings.setMaxMood(max);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.maxmood.updated")
        .add("max", max::intValue)
        .add("ttl", this.getTimeToLiveString(max))
        .add("boredaftertime", this.getBoredAfterTime(max))
        .add("sadaftertime", this.getSadAfterTime(max)));
    return true;
  }

  /**
   * Set the maximum mood level a Tamagotchi can achieve
   * Note: This value translates to days before ful decay
   * Usage: !tamagotchisettings maxhygiene [amount]
   */
  @SubCommandRoute(parentCommand = "tamagotchisettings", command = "maxhygiene")
  public boolean tamagotchisettingsCommandMaxHygiene(User user, Arguments arguments) {
    Double max = Numbers.asNumber(arguments.get(0)).toDouble();

    if (max == null || max < 0) {
      chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.maxhygiene.usage"));
      return false;
    }

    settings.setMaxHygiene(max);
    settingsService.save(settings);

    int maxAbs = max.intValue();
    chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.maxhygiene.updated")
        .add("max", maxAbs)
        .add("ttl", this.getTimeToLiveString(maxAbs)));
    return true;
  }

  /**
   * Set the maximum amount of characters a tamagotchi name can contain
   * Usage: !tamagotchisettings namemaxlength [amount]
   */
  @SubCommandRoute(parentCommand = "tamagotchisettings", command = "namemaxlength")
  public boolean tamagotchisettingsCommandNameMaxLength(User user, Arguments arguments) {
    Integer maxLength = Numbers.asNumber(arguments.get(0)).toInteger();

    if (maxLength == null || maxLength < 0) {
      chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.namemaxlength.usage"));
      return false;
    }

    settings.setNameMaxLength(maxLength);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.tamagotchisettings.namemaxlength.updated")
        .add("length", maxLength));
    return true;
  }

  private String getTimeToLiveString(double max) {
    int totalHours = (int) (max / PROPERTY_DECAY_PER_HOUR);
    return timeFormatter.timeQuantity(Duration.standardHours(totalHours), TimeUnit.HOURS);
  }

  private String getBoredAfterTime(double max) {
    int totalHours = (int) (max / PROPERTY_DECAY_PER_HOUR - max * MOOD_BORED_THRESHOLD / PROPERTY_DECAY_PER_HOUR);
    return timeFormatter.timeQuantity(Duration.standardHours(totalHours), TimeUnit.HOURS);
  }

  private String getSadAfterTime(double max) {
    int totalHours = (int) (max / PROPERTY_DECAY_PER_HOUR - max * MOOD_SAD_THRESHOLD / PROPERTY_DECAY_PER_HOUR);
    return timeFormatter.timeQuantity(Duration.standardHours(totalHours), TimeUnit.HOURS);
  }
}
