package nl.juraji.biliomi.components.system.points;

import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.system.commands.CommandService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.settings.PointsSettings;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;
import nl.juraji.biliomi.utility.types.enums.OnOff;
import nl.juraji.biliomi.utility.types.enums.StreamState;
import org.joda.time.Duration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 27-4-2017.
 * Biliomi v3
 */
@SystemComponent
@Singleton
public class PointsComponent extends Component {

  @Inject
  private PointsPayoutTimerService pointsPayoutTimer;

  @Inject
  private TimeFormatter timeFormatter;

  @Inject
  private PointsService pointsService;

  @Inject
  private CommandService commandService;

  private PointsSettings settings;

  @Override
  public void init() {
    settings = settingsService.getSettings(PointsSettings.class, s -> settings = s);
    pointsPayoutTimer.start();
  }

  /**
   * The main command for setting pointspayout settings
   * Only contains subcommand, so all calls are pushed to captureSubCommands
   * Usage: !pointssettings [pointsnames|enable|interval|amount] [more...]
   */
  @CommandRoute(command = "pointssettings", systemCommand = true)
  public boolean pointsSettingsCommand(User user, Arguments arguments) {
    return captureSubCommands("pointssettings", l10n.supply("ChatCommand.pointsSettingsCommand.usage"), user, arguments);
  }

  /**
   * Set the names for points
   * Usage: !pointssettings pointsnames [singular name] [plural name]
   */
  @SubCommandRoute(parentCommand = "pointssettings", command = "pointsnames")
  public boolean pointsSettingsCommandPointsNameSingular(User user, Arguments arguments) {
    if (!arguments.assertSize(2)) {
      chat.whisper(user, l10n.get("ChatCommand.pointsSettingsCommand.pointsNames.usage"));
      return false;
    }

    String singularName = arguments.get(0);
    String pluralName = arguments.getSafe(1);
    String oldAlias = "my" + settings.getPointsNamePlural().toLowerCase();
    String newAlias = "my" + pluralName.toLowerCase();

    settings.setPointsNameSingular(singularName);
    settings.setPointsNamePlural(pluralName);
    settingsService.save(settings);

    commandService.removeAlias(oldAlias);
    commandService.registerAlias(newAlias, "mypoints");

    chat.whisper(user, l10n.get("ChatCommand.pointsSettingsCommand.pointsNames.set")
        .add("singular", settings::getPointsNameSingular)
        .add("plural", settings::getPointsNamePlural));

    chat.whisper(user, l10n.get("ChatCommand.pointsSettingsCommand.pointsNames.set.newPointsAlias")
        .add("alias", newAlias));
    return true;
  }

  /**
   * The sub command for enabling/disabling points payouts
   * Usage: !pointssettings enablepayouts [online|offline] [on/off]
   */
  @SubCommandRoute(parentCommand = "pointssettings", command = "enablepayouts")
  public boolean pointsSettingsCommandEnable(User user, Arguments arguments) {
    StreamState streamState = EnumUtils.toEnum(arguments.get(0), StreamState.class);
    OnOff onOff = EnumUtils.toEnum(arguments.get(1), OnOff.class);

    if (streamState == null || onOff == null) {
      chat.whisper(user, l10n.get("ChatCommand.pointsSettingsCommand.enablepayouts.usage"));
      return false;
    }

    if (StreamState.ONLINE.equals(streamState)) {
      settings.setTrackOnline(OnOff.ON.equals(onOff));
    } else {
      settings.setTrackOffline(OnOff.ON.equals(onOff));
    }

    settingsService.save(settings);
    chat.whisper(user, l10n.get("ChatCommand.pointsSettingsCommand.enablepayouts.set")
        .add("streamstate", l10n.getStreamState(streamState))
        .add("state", l10n.getEnabledDisabled(onOff)));

    return true;
  }

  /**
   * The sub command for setting payout intervals
   * Usage: !pointssettings interval [online|offline] [minutes (5 at minimum)]
   */
  @SubCommandRoute(parentCommand = "pointssettings", command = "interval")
  public boolean pointsSettingsCommandInterval(User user, Arguments arguments) {
    StreamState when = EnumUtils.toEnum(arguments.get(0), StreamState.class);
    Integer intervalMinutes = Numbers.asNumber(arguments.getSafe(1)).toInteger();

    if (when == null || intervalMinutes == null || intervalMinutes < 5) {
      chat.whisper(user, l10n.get("ChatCommand.pointsSettingsCommand.interval.usage"));
      return false;
    }

    Duration duration = new Duration(intervalMinutes, 60000);

    if (StreamState.ONLINE.equals(when)) {
      settings.setOnlinePayoutInterval(duration.getMillis());
    } else {
      settings.setOfflinePayoutInterval(duration.getMillis());
    }

    settingsService.save(settings);
    chat.whisper(user, l10n.get("ChatCommand.pointsSettingsCommand.interval.set")
        .add("streamstate", l10n.getStreamState(when))
        .add("interval", timeFormatter.timeQuantity(duration.getMillis())));
    return true;
  }

  /**
   * The sub command for setting payout amounts per interval
   * Usage: !pointssettings amount [online|offline] [amount of points (1 at minimum)]
   */
  @SubCommandRoute(parentCommand = "pointssettings", command = "amount")
  public boolean pointsSettingsCommandAmount(User user, Arguments arguments) {
    StreamState when = EnumUtils.toEnum(arguments.get(0), StreamState.class);
    Long amount = Numbers.asNumber(arguments.getSafe(1)).toLong();

    if (when == null || amount == null || amount < 1) {
      chat.whisper(user, l10n.get("ChatCommand.pointsSettingsCommand.amount.usage"));
      return false;
    }

    if (StreamState.ONLINE.equals(when)) {
      settings.setOnlinePayoutAmount(amount);
    } else {
      settings.setOfflinePayoutAmount(amount);
    }

    settingsService.save(settings);
    chat.whisper(user, l10n.get("ChatCommand.pointsSettingsCommand.amount.set")
        .add("streamstate", l10n.getStreamState(when))
        .add("amount", amount));

    return true;
  }

  /**
   * Manage points
   * Only contains subcommand, so all calls are pushed to captureSubCommands
   * Usage: !managepoints [give|take|everyone] [more...]
   */
  @CommandRoute(command = "managepoints", systemCommand = true)
  public boolean managepointsCommand(User user, Arguments arguments) {
    return captureSubCommands("managepoints", l10n.supply("ChatCommand.managePoints.usage"), user, arguments);
  }

  /**
   * Give points to specific users
   * Usage: !managepoints give [username] [amount of points]
   */
  @SubCommandRoute(parentCommand = "managepoints", command = "give")
  public boolean managepointsCommandGive(User user, Arguments arguments) {
    if (!arguments.assertSize(2) || Numbers.asNumber(arguments.get(1)).isNaN()) {
      chat.whisper(user, l10n.get("ChatCommand.managePoints.give.usage"));
      return false;
    }

    User targetUser = usersService.getUser(arguments.get(0));
    if (targetUser == null) {
      chat.whisper(user, l10n.getUserNonExistent(arguments.get(0)));
      return false;
    }

    long amount = Numbers.asNumber(arguments.get(1)).toLong();
    targetUser.addPoints(amount);
    usersService.save(targetUser);

    chat.say(l10n.get("ChatCommand.managePoints.give.given")
        .add("casterusername", user::getDisplayName)
        .add("targetusername", targetUser::getDisplayName)
        .add("points", pointsService.asString(amount))
        .add("newbalance", pointsService.asString(targetUser.getPoints())));
    return true;
  }

  /**
   * Take points from specific users
   * Usage: !managepoints take [username] [amount of points]
   */
  @SubCommandRoute(parentCommand = "managepoints", command = "take")
  public boolean managepointsCommandTake(User user, Arguments arguments) {
    if (!arguments.assertSize(2) || Numbers.asNumber(arguments.get(1)).isNaN()) {
      chat.whisper(user, l10n.get("ChatCommand.managePoints.take.usage"));
      return false;
    }

    User targetUser = usersService.getUser(arguments.get(0));
    if (targetUser == null) {
      chat.whisper(user, l10n.getUserNonExistent(arguments.get(0)));
      return false;
    }

    long amount = Numbers.asNumber(arguments.get(1)).toLong();
    targetUser.takePoints(amount);
    usersService.save(targetUser);

    chat.say(l10n.get("ChatCommand.managePoints.take.taken")
        .add("casterusername", user::getDisplayName)
        .add("targetusername", targetUser::getDisplayName)
        .add("points", pointsService.asString(amount))
        .add("newbalance", pointsService.asString(targetUser.getPoints())));
    return true;
  }

  /**
   * Give points to all users currently in the chat
   * Usage: !managepoints everyone [amount of points]
   */
  @SubCommandRoute(parentCommand = "managepoints", command = "everyone")
  public boolean managepointsCommandToall(User user, Arguments arguments) {
    if (!arguments.assertSize(1) || Numbers.asNumber(arguments.get(0)).isNaN()) {
      chat.whisper(user, l10n.get("ChatCommand.managePoints.everyone.usage"));
      return false;
    }

    long amount = Numbers.asNumber(arguments.get(0)).toLong();
    List<User> currentViewers = chat.getViewers().stream()
        .map(viewer -> usersService.getUser(viewer, true))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    currentViewers.forEach(viewer -> viewer.addPoints(amount));
    usersService.save(currentViewers);

    chat.say(l10n.get("")
        .add("casterusername", user::getDisplayName)
        .add("points", pointsService.asString(amount)));
    return true;
  }
}