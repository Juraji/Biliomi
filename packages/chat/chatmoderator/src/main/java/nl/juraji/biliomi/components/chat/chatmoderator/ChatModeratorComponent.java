package nl.juraji.biliomi.components.chat.chatmoderator;

import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.system.users.UserGroupService;
import nl.juraji.biliomi.model.chat.ChatModeratorSettings;
import nl.juraji.biliomi.model.chat.ModerationAction;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.UserGroup;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;
import nl.juraji.biliomi.utility.types.enums.OnOff;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@NormalComponent
@Singleton
public class ChatModeratorComponent extends Component {

  @Inject
  private TimeFormatter timeFormatter;

  @Inject
  private UserGroupService userGroupService;

  @Inject
  private ModerationRecordService recordService;

  @Inject
  private ChatModeratorLinkPermitService linkPermitService;

  @Inject
  private ChatModeratorWorkerService moderatorWorker;

  @Inject
  private SettingsService settingsService;
  private ChatModeratorSettings settings;

  @Override
  public void init() {
    settings = settingsService.getSettings(ChatModeratorSettings.class, s -> settings = s);
    moderatorWorker.init();
  }

  /**
   * Purge users
   * Usage: !purge [username]
   */
  @CommandRoute(command = "purge", systemCommand = true, modCanActivate = true)
  public boolean purgeCommand(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.purge.usage"));
      return false;
    }

    String username = arguments.get(0);
    User targetUser = usersService.getUser(username, true);

    if (targetUser == null) {
      chat.whisper(user, l10n.getUserNonExistent(username));
      return false;
    }

    chat.purgeUser(targetUser.getUsername());
    recordService.recordModeration(targetUser, ModerationAction.PURGE);

    chat.say(l10n.get("ChatCommand.purge.purged")
        .add("username", targetUser.getDisplayName()));
    return true;
  }

  /**
   * Time users out (for 10 minutes)
   * Usage: !timeout [username]
   */
  @CommandRoute(command = "timeout", systemCommand = true, modCanActivate = true)
  public boolean timeoutCommand(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.timout.usage"));
      return false;
    }

    String username = arguments.get(0);
    User targetUser = usersService.getUser(username, true);

    if (targetUser == null) {
      chat.whisper(user, l10n.getUserNonExistent(username));
      return false;
    }

    chat.timeoutUser(targetUser.getUsername());
    recordService.recordModeration(targetUser, ModerationAction.TIMEOUT);

    chat.say(l10n.get("ChatCommand.timout.timedOut")
        .add("username", targetUser.getDisplayName()));
    return true;
  }

  /**
   * Ban users
   * Usage: !ban [username]
   */
  @CommandRoute(command = "ban", systemCommand = true, modCanActivate = true)
  public boolean banCommand(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.ban.usage"));
      return false;
    }

    String username = arguments.get(0);
    User targetUser = usersService.getUser(username, true);

    if (targetUser == null) {
      chat.whisper(user, l10n.getUserNonExistent(username));
      return false;
    }

    chat.banUser(targetUser.getUsername());
    recordService.recordModeration(targetUser, ModerationAction.BAN);

    chat.say(l10n.get("ChatCommand.ban.banned")
        .add("username", targetUser.getDisplayName()));
    return true;
  }

  /**
   * Unban users
   * Usage: !unban [username]
   */
  @CommandRoute(command = "unban", systemCommand = true, modCanActivate = true)
  public boolean unbanCommand(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.unban.usage"));
      return false;
    }

    String username = arguments.get(0);
    User targetUser = usersService.getUser(username, true);

    if (targetUser == null) {
      chat.whisper(user, l10n.getUserNonExistent(username));
      return false;
    }

    chat.unbanUser(targetUser.getUsername());
    chat.say(l10n.get("ChatCommand.unban.unbanned")
        .add("username", targetUser.getDisplayName()));
    return true;
  }

  /**
   * Permit a user to post links
   * Permits are temporary exempts from link moderation
   * Usage: Permit
   */
  @CommandRoute(command = "permit", systemCommand = true, modCanActivate = true)
  public boolean permitCommand(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.permit.usage"));
      return false;
    }

    String username = arguments.get(0);
    User targetUser = usersService.getUser(username, true);

    if (targetUser == null) {
      chat.whisper(user, l10n.getUserNonExistent(username));
      return false;
    }

    long duration = settings.getLinkPermitDuration();
    linkPermitService.addPermit(targetUser.getUsername(), duration);

    chat.say(l10n.get("ChatCommand.permit.granted")
        .add("username", targetUser::getDisplayName)
        .add("time", timeFormatter.timeQuantity(duration)));
    return true;
  }

  /**
   * Manage the link whitelist
   * Links on this whitelist don't get moderated
   * Usage: !linkWhitelist [view|add|remove] [more...]
   */
  @CommandRoute(command = "linkwhitelist", systemCommand = true)
  public boolean linkWhitelistCommand(User user, Arguments arguments) {
    return captureSubCommands("linkwhitelist", l10n.supply("ChatCommand.linkWhitelist.usage"), user, arguments);
  }

  /**
   * View the current whitelist
   * Usage: !linkwhitelist view
   */
  @SubCommandRoute(parentCommand = "linkwhitelist", command = "view")
  public boolean linkWhitelistCommandView(User user, Arguments arguments) {
    chat.whisper(user, l10n.get("ChatCommand.linkWhitelist.view.list")
        .add("list", settings.getLinkWhitelist()));
    return true;
  }

  /**
   * Add an uri to the whitelist
   * Note that links will be matched *exactly*
   * Usage: !linkwhitelist add [uri]
   */
  @SubCommandRoute(parentCommand = "linkwhitelist", command = "add")
  public boolean linkWhitelistCommandAdd(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.linkWhitelist.add.usage"));
      return false;
    }

    String uri = arguments.toString();
    settings.getLinkWhitelist().add(uri);
    settingsService.save(settings);
    chat.whisper(user, l10n.get("ChatCommand.linkWhitelist.add.added")
        .add("link", uri));
    return true;
  }

  /**
   * Add an uri to the whitelist
   * Note that links will be matched *exactly*
   * Usage: !linkwhitelist remove [uri]
   */
  @SubCommandRoute(parentCommand = "linkwhitelist", command = "remove")
  public boolean linkWhitelistCommandRemove(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.linkWhitelist.remove.usage"));
      return false;
    }

    String uri = arguments.toString();
    if (!settings.getLinkWhitelist().contains(uri)) {
      chat.whisper(user, l10n.get("ChatCommand.linkWhitelist.remove.notFound")
          .add("link", uri));
      return false;
    }

    settings.getLinkWhitelist().remove(uri);
    settingsService.save(settings);
    chat.whisper(user, l10n.get("ChatCommand.linkWhitelist.remove.removed")
        .add("link", uri));
    return true;
  }

  /**
   * Manage automated chat moderator settings
   * Usage: !chatmoderator [linksallowed|linkpermittime|capsallowed|capstrigger|capsratio|repititionallowed|repititiontrigger] [more...]
   */
  @CommandRoute(command = "chatmoderator", systemCommand = true)
  public boolean chatmoderatorCommand(User user, Arguments arguments) {
    return captureSubCommands("chatmoderator", l10n.supply("ChatCommand.chatmoderator.usage"), user, arguments);
  }

  /**
   * Toggle allowing normal users to post links
   * Usage: !chatmoderator linksallowed [on|off]
   */
  @SubCommandRoute(parentCommand = "chatmoderator", command = "linksallowed")
  public boolean chatmoderatorCommandLinksAllowed(User user, Arguments arguments) {
    OnOff onOff = EnumUtils.toEnum(arguments.get(0), OnOff.class);

    if (onOff == null) {
      chat.whisper(user, l10n.get("ChatCommand.chatmoderator.linksAllowed.usage"));
      return false;
    }

    settings.setLinksAllowed(OnOff.ON.equals(onOff));
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.chatmoderator.linksAllowed.saved")
        .add("state", l10n.getAllowedDisallowed(settings.isLinksAllowed())));
    return true;
  }

  /**
   * Set the duration of a link permit
   * Usage: !chatmoderator linkpermittime [time in minutes]
   */
  @SubCommandRoute(parentCommand = "chatmoderator", command = "linkpermittime")
  public boolean chatmoderatorCommandLinkPermitTime(User user, Arguments arguments) {
    Long minutes = Numbers.asNumber(arguments.get(0)).toLong();

    if (minutes == null) {
      chat.whisper(user, l10n.get("ChatCommand.chatmoderator.linkPermitTime.usage"));
      return false;
    }

    long timeInMillis = TimeUnit.MILLISECONDS.convert(minutes, TimeUnit.MINUTES);
    settings.setLinkPermitDuration(timeInMillis);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.chatmoderator.linkPermitTime.saved")
        .add("time", timeFormatter.timeQuantity(timeInMillis)));
    return true;
  }

  /**
   * Toggle allowing excessive caps usage
   * Usage: !chatmoderator capsallowed [on|off]
   */
  @SubCommandRoute(parentCommand = "chatmoderator", command = "capsallowed")
  public boolean chatmoderatorCommandCapsAllowed(User user, Arguments arguments) {
    OnOff onOff = EnumUtils.toEnum(arguments.get(0), OnOff.class);

    if (onOff == null) {
      chat.whisper(user, l10n.get("ChatCommand.chatmoderator.capsAllowed.usage"));
      return false;
    }

    settings.setExcessiveCapsAllowed(OnOff.ON.equals(onOff));
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.chatmoderator.capsAllowed.saved")
        .add("state", l10n.getAllowedDisallowed(settings.isExcessiveCapsAllowed())));
    return true;
  }

  /**
   * Set the amount of caps a message should contain before triggering moderation
   * Usage: !chatmoderator capstrigger [amount of caps]
   */
  @SubCommandRoute(parentCommand = "chatmoderator", command = "capstrigger")
  public boolean chatmoderatorCommandCapsTrigger(User user, Arguments arguments) {
    Integer count = Numbers.asNumber(arguments.get(0)).toInteger();

    if (count == null) {
      chat.whisper(user, l10n.get("ChatCommand.chatmoderator.capsTrigger.usage"));
      return false;
    }

    settings.setCapsTrigger(count);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.chatmoderator.capsTrigger.saved")
        .add("count", count));
    return true;
  }

  /**
   * Set the ratio (percentage) of caps a message should contain before triggering moderation
   * Usage: !chatmoderator capsratio [ratio 0-100]
   */
  @SubCommandRoute(parentCommand = "chatmoderator", command = "capsratio")
  public boolean chatmoderatorCommandCapsRatio(User user, Arguments arguments) {
    Integer percentage = Numbers.asNumber(arguments.get(0)).toInteger();

    if (percentage == null || percentage < 0 || percentage > 100) {
      chat.whisper(user, l10n.get("ChatCommand.chatmoderator.capsRatio.usage"));
      return false;
    }

    double ratio = percentage / 100.0;
    settings.setCapsTriggerRatio(ratio);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.chatmoderator.capsRatio.saved")
        .add("percentage", MathUtils.doubleToPercentage(ratio)));
    return true;
  }

  /**
   * Toggle allowing repitition of characters
   * Usage: !chatmoderator repititionallowed [on|off]
   */
  @SubCommandRoute(parentCommand = "chatmoderator", command = "repititionallowed")
  public boolean chatmoderatorCommandRepititionAllowed(User user, Arguments arguments) {
    OnOff onOff = EnumUtils.toEnum(arguments.get(0), OnOff.class);

    if (onOff == null) {
      chat.whisper(user, l10n.get("ChatCommand.chatmoderator.repititionAllowed.usage"));
      return false;
    }

    settings.setRepeatedCharactersAllowed(OnOff.ON.equals(onOff));
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.chatmoderator.repititionAllowed.saved")
        .add("state", l10n.getAllowedDisallowed(settings.isRepeatedCharactersAllowed())));
    return true;
  }

  /**
   * Set the amount of repeating characters a message should contain before triggering moderation
   * Usage: !chatmoderator repititiontrigger [amount of similar characters]
   */
  @SubCommandRoute(parentCommand = "chatmoderator", command = "repititiontrigger")
  public boolean chatmoderatorCommandRepititionTrigger(User user, Arguments arguments) {
    Integer count = Numbers.asNumber(arguments.get(0)).toInteger();

    if (count == null) {
      chat.whisper(user, l10n.get("ChatCommand.chatmoderator.repititionTrigger.usage"));
      return false;
    }

    settings.setRepeatedCharacterTrigger(count);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.chatmoderator.repititionTrigger.saved")
        .add("count", count));
    return true;
  }

  /**
   * Exempt users within a group
   * Uses the same groupweight logic as the command router
   * Usage: !chatmoderator exemptgroup [group name or "off" to disable]
   */
  @SubCommandRoute(parentCommand = "chatmoderator", command = "exemptgroup")
  public boolean chatmoderatorCommandExemptGroupWeight(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.chatmoderator.exemptGroup.usage"));
      return false;
    }

    String groupName = arguments.get(0);
    if ("off".equalsIgnoreCase(groupName)) {
      settings.setExemptedGroup(null);
      settingsService.save(settings);

      chat.whisper(user, l10n.get("ChatCommand.chatmoderator.exemptGroup.disabled"));
      return true;
    }

    UserGroup targetGroup = userGroupService.getByName(groupName);
    if (targetGroup == null) {
      chat.whisper(user, l10n.get("ChatCommand.chatmoderator.exemptGroup.usage"));
      return false;
    }

    settings.setExemptedGroup(targetGroup);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.chatmoderator.exemptGroup.saved")
        .add("group", targetGroup::getName));
    return true;
  }

  /**
   * Set the moderation action to perform on each strike
   * A strike is analog to one of the following: Nothing, Purge, Timeout or Ban.
   * For example: Set strikes to "Purge Purge Timeout" will purge the
   * user on the first two strikes, then timeout on third strike and beyond.
   * Strikes are tracked per user for a time of one hour since first strike.
   * Usage: !chatmoderator setstrikes [strike 1] [strike 2] [strike 3]
   */
  @SubCommandRoute(parentCommand = "chatmoderator", command = "setstrikes")
  public boolean chatmoderatorCommandSetStrikes(User user, Arguments arguments) {
    ModerationAction strike1 = EnumUtils.toEnum(arguments.get(0), ModerationAction.class);
    ModerationAction strike2 = EnumUtils.toEnum(arguments.get(1), ModerationAction.class);
    ModerationAction strike3 = EnumUtils.toEnum(arguments.get(2), ModerationAction.class);

    if (strike1 == null || strike2 == null || strike3 == null) {
      chat.whisper(user, l10n.get("ChatCommand.chatmoderator.setStrikes.usage"));
      return false;
    }

    settings.setFirstStrike(strike1);
    settings.setSecondStrike(strike2);
    settings.setThirdStrike(strike3);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.chatmoderator.setStrikes.saved")
        .add("firststrike", EnumUtils.pretty(strike1))
        .add("secondstrike", EnumUtils.pretty(strike2))
        .add("thirdstrike", EnumUtils.pretty(strike1)));
    return true;
  }
}
