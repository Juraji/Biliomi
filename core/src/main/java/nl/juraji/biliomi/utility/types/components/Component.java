package nl.juraji.biliomi.utility.types.components;

import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.commandrouters.annotations.CliCommandRouteImplementor;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRouteImplementor;
import nl.juraji.biliomi.utility.commandrouters.routers.SubCommandRouter;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.commandrouters.types.CommandRouteInvocationException;
import nl.juraji.biliomi.utility.commandrouters.types.CommandRouteNotFoundException;
import nl.juraji.biliomi.utility.types.Init;
import nl.juraji.biliomi.utility.types.collections.I18nMap;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.function.Supplier;

/**
 * Created by Juraji on 27-4-2017.
 * Biliomi v3
 */
@CommandRouteImplementor
@CliCommandRouteImplementor
public abstract class Component implements Init {

  private final SubCommandRouter subCommandRouter;

  @Inject
  protected Logger logger;

  @Inject
  protected SettingsService settingsService;

  @Inject
  protected ChatService chat;

  @Inject
  protected UsersService usersService;

  @Inject
  protected I18nMap i18n;

  public Component() {
    subCommandRouter = new SubCommandRouter(this);
  }

  @PostConstruct
  private void initComponent() {
    try {
      subCommandRouter.buildRoutes();
    } catch (Exception e) {
      logger.error("A problem occurred while collecting subcommand routes", e);
    }
  }

  @Override
  public void init() {
    // Do nothing by default
  }

  /**
   * Convenience method for when a command method only proxies to subcommands
   *
   * @param parentCommand        The parent command to use
   * @param usageMessageSupplier A message supplier stating the usage of the command
   *                             This is whispered to the user when a called subcommand does not exist
   * @param user                 The user executing the command
   * @param arguments            The Arguments obj containing the ORIGINAL arguments
   * @return True on command succes, False on command failed, not found or error
   */
  protected boolean captureSubCommands(String parentCommand, Supplier<String> usageMessageSupplier, User user, Arguments arguments) {
    if (!arguments.assertMinSize(1)) {
      chat.whisper(user, usageMessageSupplier.get());
      return false;
    }

    try {
      String subCommand = arguments.pop();
      return subCommandRouter.invokeSubCommand(parentCommand, subCommand, user, arguments);
    } catch (CommandRouteNotFoundException e) {
      if (usageMessageSupplier != null) {
        chat.whisper(user, usageMessageSupplier.get());
      }
    } catch (CommandRouteInvocationException e) {
      logger.error("An error occurred while invoking subcommand executor", e);
    }

    return false;
  }
}
