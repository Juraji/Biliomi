package nl.juraji.biliomi.utility.commandrouters.routers;

import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.system.commands.CommandService;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.model.core.Command;
import nl.juraji.biliomi.model.core.CommandHistoryRecordDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.irc.user.messages.IrcMessageEvent;
import nl.juraji.biliomi.utility.commandrouters.types.CommandCall;
import nl.juraji.biliomi.utility.commandrouters.types.RegistryEntry;
import nl.juraji.biliomi.utility.estreams.EBiStream;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.types.collections.L10nMap;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by Juraji on 28-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class CommandRouter {

  @Inject
  private CommandRouterRegistry routerRegistry;

  @Inject
  private CommandHistoryRecordDao commandHistoryRecordDao;

  @Inject
  private Logger logger;

  @Inject
  private L10nMap l10n;

  @Inject
  private CommandService commandService;

  @Inject
  private UsersService userService;

  @Inject
  private PointsService pointsService;

  @Inject
  private TimeFormatter timeFormatter;

  @Inject
  private ChatService chat;

  /**
   * Check for command eligibility and run it of everything checks out
   *
   * @param event The MessageEvent containing the command
   */
  public boolean runCommand(IrcMessageEvent event, boolean calledByApi) {
    // Message is not command-like, drop it
    if (!CommandCall.isCallable(event.getMessage())) {
      return false;
    }

    CommandCall commandCall = new CommandCall(event.getMessage());
    // Command is not known or disabled, drop it
    if (!routerRegistry.containsKey(commandCall.getCommand())) {
      return false;
    }

    Command command = commandService.getCommand(commandService.translateAlias(commandCall.getCommand()));
    User user = userService.getUser(event.getUsername(), true);

    if (!calledByApi) {
      // Check permissions
      if (isPermissionDenied(user, command)) {
        chat.whisper(user, l10n.get("CommandCheck.notEligible.noRights")
            .add("command", command::getCommand));
        return false;
      }

      // Check cooldown
      long remaining = getCooldown(user, command);
      if (command.getCooldown() > 0 && remaining > 0) {
        // The command has a cooldown, check the service if a cooldown exists for this user
        chat.whisper(user, l10n.get("CommandCheck.notEligible.cooldownActive")
            .add("command", command::getCommand)
            .add("remaining", timeFormatter.timeQuantity(remaining)));
        return false;
      }

      // Check points cost
      if (command.getPrice() > 0 && !pointsWithdrawalOK(user, command)) {
        // Command has a price, check if user has enough to proceed
        // Cooldown might be set, revert it
        commandService.clearCooldown(user, command);
        chat.whisper(user, l10n.get("CommandCheck.notEligible.notEnoughPoints")
            .add("command", command::getCommand)
            .add("cost", pointsService.asString(command.getPrice()))
            .add("balance", pointsService.asString(user.getPoints())));
        return false;
      }
    }

    RegistryEntry entry = routerRegistry.get(commandCall.getCommand());
    boolean commandSuccess = false;

    try {
      if (entry == null) {
        // Thrown, so it gets caught by the message in the catch
        throw new IllegalStateException("Could not fetch register entry for " + commandCall.getCommand());
      }

      // No worries about method interface here, since it has already been asserted when it was registered
      commandSuccess = (boolean) entry.getMethod().invoke(entry.getComponentInstance(), user, commandCall.getArguments());
    } catch (Exception e) {
      chat.say(l10n.get("Common.errors.catchedFatalError")
          .add("username", user::getDisplayName)
          .add("command", command::getCommand)
          .add("errormessage", e::getMessage));
      logger.error("Failed invoking command executor for " + commandCall.getCommand(), e);
    }

    if (!calledByApi) {
      commandHistoryRecordDao.recordCommand(commandCall, command, user, commandSuccess);
    }

    if (!commandSuccess) {
      // Command was not a success
      // Return any taken points
      if (command.getPrice() > 0) {
        pointsService.give(user, command.getPrice());
      }
      // Clear any cooldown
      if (command.getCooldown() > 0) {
        commandService.clearCooldown(user, command);
      }
    }

    return commandSuccess;
  }

  /**
   * Defined in method because it's easier that way
   */
  private boolean isPermissionDenied(User user, Command command) {
    boolean userLacksPerm = true;

    if (user.isCaster()) {
      // Caster can always execute a command
      userLacksPerm = false;
    } else if (command.isSystemCommand()) {
      // If the command is a system command
      // then check if the command is activatable by moderators and the user is a moderator
      userLacksPerm = !(command.isModeratorCanActivate() && user.isModerator());
    } else if (command.isModeratorCanActivate() && user.isModerator() || user.getUserGroup().isInGroup(command.getUserGroup())) {
      userLacksPerm = false;
    }

    return userLacksPerm;
  }

  private long getCooldown(User user, Command command) {
    long remaining = commandService.getRemainingCooldown(user, command);
    if (remaining > 0) {
      // A cooldown is active for the user
      return remaining;
    } else if (command.getCooldown() > 0) {
      // Set a new cooldown for the user and command
      commandService.setCooldown(user, command);
    }

    return -1;
  }

  private boolean pointsWithdrawalOK(User user, Command command) {
    // The service was not able to take the required amount of points from this user
    return pointsService.take(user, command.getPrice()) != -1;
  }

  /**
   * Get a BiEStream of CommandRoute annotations and the annotated metods for a class
   * componentClass can be any class for compatibility with interceptors
   *
   * @param componentClass The class to scan
   * @return A BiEStream
   */
  public static <A extends Annotation> EBiStream<A, Method, Exception> findCommandMethods(Class<?> componentClass, Class<A> annotation) {
    return EStream.from(componentClass.getDeclaredMethods())
        .filter(method -> method.isAnnotationPresent(annotation))
        .mapToBiEStream(method -> method.getAnnotation(annotation), method -> method);
  }
}
