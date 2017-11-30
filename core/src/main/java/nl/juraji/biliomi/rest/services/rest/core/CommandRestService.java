package nl.juraji.biliomi.rest.services.rest.core;

import nl.juraji.biliomi.model.core.Command;
import nl.juraji.biliomi.model.core.CommandDao;
import nl.juraji.biliomi.model.core.CustomCommand;
import nl.juraji.biliomi.rest.config.ModelRestService;
import nl.juraji.biliomi.utility.commandrouters.routers.CommandRouterRegistry;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 17-6-2017.
 * Biliomi v3
 */
@Path("/core/commands")
public class CommandRestService extends ModelRestService<Command> {

  @Inject
  private CommandDao commandDao;

  @Inject
  private CommandRouterRegistry commandRouterRegistry;

  @Override
  public List<Command> getEntities() {

    return commandDao.getList().stream()
        .filter(command -> !CustomCommand.class.isAssignableFrom(command.getClass()))
        .collect(Collectors.toList());
  }

  @Override
  public Command getEntity(long id) {
    return commandDao.get(id);
  }

  @Override
  public Command createEntity(Command e) {
    throw new ForbiddenException();
  }

  @Override
  public Command updateEntity(Command e, long id) {
    Command command = commandDao.get(id);
    String commandStr = command.getCommand();

    // Only some of the properties can be changed
    command.setPrice(e.getPrice());
    command.setCooldown(e.getCooldown());
    command.setModeratorCanActivate(e.isModeratorCanActivate());

    if (e.getUserGroup() != null) {
      command.setUserGroup(e.getUserGroup());
    }

    // Clear current aliasses for command
    if (!command.getAliasses().isEmpty()) {
      command.getAliasses().clear();
      commandRouterRegistry.clearAliassesFor(commandStr);
    }

    if (!e.getAliasses().isEmpty()) {
      // Re-add all aliasses for command
      command.getAliasses().addAll(e.getAliasses());
      e.getAliasses().forEach(alias -> commandRouterRegistry.putAlias(alias, commandStr));
    }

    commandDao.save(command);
    return command;
  }

  @Override
  public boolean deleteEntity(long id) {
    throw new ForbiddenException();
  }
}
