package nl.juraji.biliomi.rest.services.rest.core;

import nl.juraji.biliomi.model.core.CustomCommand;
import nl.juraji.biliomi.model.core.CustomCommandDao;
import nl.juraji.biliomi.components.system.commands.CustomCommandsComponent;
import nl.juraji.biliomi.rest.config.ModelRestService;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.routers.CommandRouterRegistry;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Created by Juraji on 17-6-2017.
 * Biliomi v3
 */
@Path("/core/customcommands")
public class CustomCommandRestService extends ModelRestService<CustomCommand> {

  @Inject
  private CustomCommandDao customCommandDao;

  @Inject
  private CommandRouterRegistry commandRouterRegistry;

  @Inject
  @SystemComponent
  private CustomCommandsComponent customCommandsComponent;

  @Override
  public List<CustomCommand> getEntities() {
    return customCommandDao.getList();
  }

  @Override
  public CustomCommand getEntity(long id) {
    return customCommandDao.get(id);
  }

  @Override
  public CustomCommand createEntity(CustomCommand e) {
    customCommandDao.save(e);

    // Register new command in the commandrouter
    commandRouterRegistry.put(e.getCommand(), customCommandsComponent, CustomCommandsComponent.customCommandRunnerMethod);
    e.getAliasses().forEach(alias -> commandRouterRegistry.putAlias(alias, e.getCommand()));

    return e;
  }

  @Override
  public CustomCommand updateEntity(CustomCommand e, long id) {
    CustomCommand customCommand = customCommandDao.get(id);
    String command = e.getCommand();

    // Only some of the properties can be changed
    customCommand.setPrice(e.getPrice());
    customCommand.setCooldown(e.getCooldown());
    customCommand.setModeratorCanActivate(e.isModeratorCanActivate());
    customCommand.setSystemCommand(e.isSystemCommand());
    customCommand.setUserGroup(e.getUserGroup());
    customCommand.setMessage(e.getMessage());

    // Clear current aliasses for command
    if (!customCommand.getAliasses().isEmpty()) {
      customCommand.getAliasses().clear();
      commandRouterRegistry.clearAliassesFor(command);
    }

    if (!e.getAliasses().isEmpty()) {
      // Re-add all aliasses for command
      customCommand.getAliasses().addAll(e.getAliasses());
      e.getAliasses().forEach(alias -> commandRouterRegistry.putAlias(alias, command));
    }

    customCommandDao.save(customCommand);
    return customCommand;
  }

  @Override
  public boolean deleteEntity(long id) {
    CustomCommand customCommand = customCommandDao.get(id);

    if (customCommand == null) {
      return false;
    }

    // Remove command from registry, so it won't be recognized as valid command anymore
    commandRouterRegistry.remove(customCommand.getCommand());
    customCommand.getAliasses().forEach(commandRouterRegistry::removeAlias);

    customCommandDao.delete(customCommand);
    return true;
  }
}
