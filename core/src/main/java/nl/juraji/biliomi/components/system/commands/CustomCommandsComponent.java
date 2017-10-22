package nl.juraji.biliomi.components.system.commands;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.system.users.UserGroupService;
import nl.juraji.biliomi.model.core.CustomCommand;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.routers.CommandRouter;
import nl.juraji.biliomi.utility.commandrouters.routers.CommandRouterRegistry;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Method;

/**
 * Created by Juraji on 4-5-2017.
 * Biliomi v3
 */
@SystemComponent
@Singleton
public class CustomCommandsComponent extends Component {

  public static final Method customCommandRunnerMethod;

  static {
    // Keep a final reference to the command runner method, to be able to register custom commands in the commandService
    Method customCommandRunner = null;

    try {
      customCommandRunner = CustomCommandsComponent.class
          .getDeclaredMethod("customCommandRunner", User.class, Arguments.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      BiliomiContainer.getContainer().shutdownInError();
    }

    customCommandRunnerMethod =  customCommandRunner;
  }

  @Inject
  private CommandService commandService;

  @Inject
  private CommandRouterRegistry commandRouterRegistry;

  @Inject
  private UserGroupService userGroupService;

  @Inject
  private CommandRouter commandRouter;

  @Inject
  private PointsService pointsService;

  @Inject
  private TimeFormatter timeFormatter;

  @Override
  public void init() {
    // Get all previously persisted custom commands and register them with the commandService
    commandService.getAllCustomCommands()
        .forEach(customCommand -> {
          commandRouterRegistry.put(customCommand.getCommand(), this, customCommandRunnerMethod);
          customCommand.getAliasses().forEach(alias -> commandRouterRegistry.putAlias(alias, customCommand.getCommand()));
        });
  }

  /**
   * Manage custom commands
   * Only contains subcommand, so all calls are pushed to captureSubCommands
   * Command attributes can be editted useing !editcommand
   * Usage: !customcommand [help|add|edit|remove] [command] [more...]
   */
  @CommandRoute(command = "customcommand", systemCommand = true)
  public boolean customCommandCommand(User user, Arguments arguments) {
    return captureSubCommands("customcommand", i18n.supply("ChatCommand.customCommand.usage"), user, arguments);
  }

  /**
   * Get some info on custom commands
   * Usage: !customcommand help
   */
  @SubCommandRoute(parentCommand = "customcommand", command = "help")
  public boolean customCommandCommandHelp(User user, Arguments arguments) {
    chat.whisper(user, i18n.get("ChatCommand.customCommand.help.message"));
    return true;
  }

  /**
   * Add custom commands
   * Usage: !customcommand add [command] [message...]
   */
  @SubCommandRoute(parentCommand = "customcommand", command = "add")
  public boolean customCommandCommandAdd(User user, Arguments arguments) {
    if (!arguments.assertMinSize(2)) {
      chat.whisper(user, i18n.get("ChatCommand.customCommand.add.usage"));
      return false;
    }

    // Pop the first argument as command key
    String newCommand = arguments.popSafe().toLowerCase();
    if (commandService.commandExists(newCommand)) {
      chat.whisper(user, i18n.get("ChatCommand.customCommand.add.commandAlreadyExists")
          .add("command", newCommand));
      return false;
    }

    // The remainging arguments are part of the command message
    String message = arguments.toString();

    CustomCommand customCommand = new CustomCommand();
    customCommand.setCommand(newCommand);
    customCommand.setMessage(message);
    customCommand.setUserGroup(userGroupService.getDefaultGroup());
    commandService.save(customCommand);

    // Register the new command so it is known and can be used immediately
    commandRouterRegistry.put(customCommand.getCommand(), this, customCommandRunnerMethod);

    chat.whisper(user, i18n.get("ChatCommand.customCommand.add.added")
        .add("command", customCommand::getCommand)
        .add("message", customCommand::getMessage));
    return true;
  }

  /**
   * Edit custom command messages
   * Usage: !customcommand edit [command] [new message...]
   */
  @SubCommandRoute(parentCommand = "customcommand", command = "edit")
  public boolean customCommandCommandEditmessage(User user, Arguments arguments) {
    if (!arguments.assertMinSize(2)) {
      chat.whisper(user, i18n.get("ChatCommand.customCommand.edit.usage"));
      return false;
    }

    String targetCommand = arguments.pop();
    CustomCommand customCommand = commandService.getCustomCommand(targetCommand);
    if (customCommand == null) {
      chat.whisper(user, i18n.getCommandNonExistent(targetCommand));
      return false;
    }

    // The remainging arguments are part of the new command message
    String newMessage = arguments.toString();
    customCommand.setMessage(newMessage);

    commandService.save(customCommand);

    chat.whisper(user, i18n.get("ChatCommand.customCommand.edit.edited")
        .add("command", customCommand::getCommand)
        .add("message", customCommand::getMessage));
    return true;
  }

  /**
   * Remove custom commands
   * Usage: !customcommand remove [command]
   */
  @SubCommandRoute(parentCommand = "customcommand", command = "remove")
  public boolean customCommandCommandRemove(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, i18n.get("ChatCommand.customCommand.remove.usage"));
      return false;
    }

    CustomCommand customCommand = commandService.getCustomCommand(arguments.get(0));
    if (customCommand == null) {
      chat.whisper(user, i18n.getCommandNonExistent(arguments.get(0)));
      return false;
    }

    customCommand.getAliasses().forEach(commandRouterRegistry::removeAlias);
    commandRouterRegistry.remove(customCommand.getCommand());
    commandService.delete(customCommand);

    chat.whisper(user, i18n.get("ChatCommand.customCommand.remove.removed")
        .add("command", customCommand::getCommand));
    return true;
  }

  /**
   * Use the CustomCommandRunner to run custom commands
   * All custom command entries in the registry point to this method
   */
  @SuppressWarnings("unused") // Is used by reflection in init()
  public boolean customCommandRunner(User user, Arguments arguments) {
    CustomCommand customCommand = commandService.getCustomCommand(arguments.getCommand());

    if (customCommand == null) {
      logger.error("Could not find persisted custom command {}!", arguments.getCommand());
      return false;
    }

    Templater template = Templater.template(customCommand.getMessage());

    // Caller information
    template.add("callername", user::getNameAndTitle);
    template.add("callerpoints", pointsService.asString(user.getPoints()));
    template.add("callertime", timeFormatter.timeQuantity(user.getRecordedTime()));
    template.add("callergroup", user.getUserGroup().getName());

    // Command Arguments
    template.add("firstargument", arguments.getSafe(0));
    template.add("allarguments", arguments::toString);

    template.add("firstargumentasuser", () -> {
      User argUser = usersService.getUser(arguments.get(0), true);
      if (argUser == null) {
        return "!!!UnknownUser";
      } else {
        return argUser.getDisplayName();
      }
    });

    chat.say(template);
    return true;
  }
}
