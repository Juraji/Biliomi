package nl.juraji.biliomi.components.system.commands;

import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.system.users.UserGroupService;
import nl.juraji.biliomi.model.core.Command;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.UserGroup;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;
import nl.juraji.biliomi.utility.types.enums.AddRemove;
import nl.juraji.biliomi.utility.types.enums.OnOff;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 4-5-2017.
 * Biliomi v3
 */
@SystemComponent
@Singleton
public class CommandsManagementComponent extends Component {

  @Inject
  private CommandService commandService;

  @Inject
  private TimeFormatter timeFormatter;

  @Inject
  private PointsService pointsService;

  @Inject
  private UserGroupService userGroupService;

  /**
   * Clear all command cooldowns for a specific user.
   * Usage: !clearcooldownfor [username]
   */
  @CommandRoute(command = "clearcooldownfor", systemCommand = true)
  public boolean clearCooldownForCommand(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.clearCooldownFor.usage"));
      return false;
    }

    User targetUser = usersService.getUser(arguments.get(0));
    if (targetUser == null) {
      chat.whisper(user, l10n.getUserNonExistent(arguments.get(0)));
      return false;
    }

    commandService.clearCooldownFor(targetUser);
    chat.whisper(user, l10n.get("ChatCommand.clearCooldownFor.cleared")
        .add("username", targetUser::getDisplayName));
    return true;
  }

  /**
   * Edit commands
   * Only contains subcommand, so all calls are pushed to captureSubCommands
   * Usage: !editcommand [cooldown|price|modcanalwaysactivate|group|alias] [command] [more...]
   */
  @CommandRoute(command = "editcommand", systemCommand = true)
  public boolean editCommandCommand(User user, Arguments arguments) {
    return captureSubCommands("editcommand", l10n.supply("ChatCommand.editCommand.usage"), user, arguments);
  }

  /**
   * Set command cooldown
   * Usage: !editcommand cooldown [command] [cooldown in seconds]
   */
  @SubCommandRoute(parentCommand = "editcommand", command = "cooldown")
  public boolean editCommandCommandCooldown(User user, Arguments arguments) {
    if (!arguments.assertSize(2)) {
      chat.whisper(user, l10n.get("ChatCommand.editCommand.cooldown.usage"));
      return false;
    }

    Command command = commandService.getCommand(arguments.get(0));
    if (command == null) {
      chat.whisper(user, l10n.getCommandNonExistent(arguments.get(0)));
      return false;
    }

    if (command.isSystemCommand()) {
      chat.whisper(user, l10n.get("Common.editCommand.failOnSystemCommand")
          .add("command", arguments.get(0)));
      return false;
    }

    Integer newCooldownSeconds = Numbers.asNumber(arguments.get(1)).toInteger();
    if (newCooldownSeconds == null) {
      chat.whisper(user, l10n.get("ChatCommand.editCommand.cooldown.usage"));
      return false;
    }

    long newCooldown = newCooldownSeconds.longValue() * 1000;
    command.setCooldown(newCooldown);
    commandService.save(command);

    chat.whisper(user, l10n.get("ChatCommand.editCommand.cooldown.set")
        .add("command", command::getCommand)
        .add("time", timeFormatter.timeQuantity(command.getCooldown())));
    return true;
  }

  /**
   * Set command price in points
   * Usage: !editcommand price [command] [amount of points]
   */
  @SubCommandRoute(parentCommand = "editcommand", command = "price")
  public boolean editCommandCommandPrice(User user, Arguments arguments) {
    Long newPrice = Numbers.asNumber(arguments.get(1)).toLong();
    if (!arguments.assertSize(2) || newPrice == 0) {
      chat.whisper(user, l10n.get("ChatCommand.editCommand.price.usage"));
      return false;
    }

    Command command = commandService.getCommand(arguments.get(0));
    if (command == null) {
      chat.whisper(user, l10n.getCommandNonExistent(arguments.get(0)));
      return false;
    }

    if (command.isSystemCommand()) {
      chat.whisper(user, l10n.get("Common.editCommand.failOnSystemCommand")
          .add("command", arguments.get(0)));
      return false;
    }

    command.setPrice(newPrice);
    commandService.save(command);

    chat.whisper(user, l10n.get("ChatCommand.editCommand.price.set")
        .add("command", command::getCommand)
        .add("points", pointsService.asString(command.getPrice())));
    return true;
  }

  /**
   * Set the command user group
   * A user needs to be in the group or higher in order to be able to execute the command
   * Usage: !editcommand group [command] [groupname]
   */
  @SubCommandRoute(parentCommand = "editcommand", command = "group")
  public boolean editCommandCommandGroup(User user, Arguments arguments) {
    if (!arguments.assertSize(2)) {
      chat.whisper(user, l10n.get("ChatCommand.editCommand.group.usage"));
      return false;
    }

    Command command = commandService.getCommand(arguments.get(0));
    if (command == null) {
      chat.whisper(user, l10n.getCommandNonExistent(arguments.get(0)));
      return false;
    }

    if (command.isSystemCommand()) {
      chat.whisper(user, l10n.get("Common.editCommand.failOnSystemCommand")
          .add("command", arguments.get(0)));
      return false;
    }

    UserGroup userGroup = userGroupService.getByName(arguments.get(1));
    if (userGroup == null) {
      chat.whisper(user, l10n.get("ChatCommand.editCommand.group.groupNotFound")
          .add("groupname", arguments.get(1)));
      return false;
    }

    command.setUserGroup(userGroup);
    commandService.save(command);

    chat.whisper(user, l10n.get("ChatCommand.editCommand.group.set")
        .add("command", command::getCommand)
        .add("groupname", userGroup::getName));
    return true;
  }

  /**
   * Set moderators can activate
   * Enables mod to always be able to activate the command, regardless of command group or system commands
   * Usage: !editcommand modcanalwaysactivate [command] [on/off]
   */
  @SubCommandRoute(parentCommand = "editcommand", command = "cooldown")
  public boolean editCommandCommandModCanAlwaysActivate(User user, Arguments arguments) {
    if (!arguments.assertSize(2)) {
      chat.whisper(user, l10n.get("ChatCommand.editCommand.modCanAlwaysActivate.usage"));
      return false;
    }

    Command command = commandService.getCommand(arguments.get(0));
    if (command == null) {
      chat.whisper(user, l10n.getCommandNonExistent(arguments.get(0)));
      return false;
    }

    OnOff onOff = EnumUtils.toEnum(arguments.get(1), OnOff.class);
    if (onOff == null) {
      chat.whisper(user, l10n.get("ChatCommand.editCommand.modCanAlwaysActivate.usage"));
      return false;
    }

    command.setModeratorCanActivate(OnOff.ON.equals(onOff));
    if (command.isModeratorCanActivate()) {
      chat.whisper(user, l10n.get("ChatCommand.editCommand.modCanAlwaysActivate.set.on")
          .add("command", command::getCommand));
    } else {
      chat.whisper(user, l10n.get("ChatCommand.editCommand.modCanAlwaysActivate.set.off")
          .add("command", command::getCommand));
    }
    return true;
  }

  /**
   * Set/remove command aliasses
   * Usage: !editcommand alias [command] [add/remove] [alias]
   */
  @SubCommandRoute(parentCommand = "editcommand", command = "alias")
  public boolean editCommandCommandAlias(User user, Arguments arguments) {
    if (!arguments.assertSize(3) || arguments.getSafe(0).equalsIgnoreCase(arguments.get(2))) {
      chat.whisper(user, l10n.get("ChatCommand.editCommand.alias.usage"));
      return false;
    }

    AddRemove addRemove = EnumUtils.toEnum(arguments.get(1), AddRemove.class);
    if (addRemove == null) {
      chat.whisper(user, l10n.get("ChatCommand.editCommand.alias.usage"));
      return false;
    }

    Command command = commandService.getCommand(arguments.get(0));
    if (command == null) {
      chat.whisper(user, l10n.getCommandNonExistent(arguments.get(0)));
      return false;
    }

    String alias = arguments.getSafe(2).toLowerCase();
    if (AddRemove.ADD.equals(addRemove)) {
      // Add alias
      if (!commandService.registerAlias(alias, command)) {
        chat.whisper(user, l10n.get("ChatCommand.editCommand.alias.add.aliasAlreadyExists")
            .add("alias", alias));
        return false;
      }

      chat.whisper(user, l10n.get("ChatCommand.editCommand.alias.add.added")
          .add("alias", alias)
          .add("command", command::getCommand));
    } else if (AddRemove.REMOVE.equals(addRemove)) {
      // Remove alias
      if (!command.getAliasses().contains(alias)) {
        chat.whisper(user, l10n.get("ChatCommand.editCommand.alias.remove.nonExistent")
            .add("command", command::getCommand)
            .add("alias", alias));
        return false;
      }

      commandService.removeAlias(alias, command);
      chat.whisper(user, l10n.get("ChatCommand.editCommand.alias.remove.removed")
          .add("alias", alias)
          .add("command", command::getCommand));
    }

    return true;
  }
}
