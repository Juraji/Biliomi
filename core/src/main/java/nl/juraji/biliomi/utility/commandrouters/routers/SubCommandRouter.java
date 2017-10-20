package nl.juraji.biliomi.utility.commandrouters.routers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.commandrouters.types.CommandRouteInvocationException;
import nl.juraji.biliomi.utility.commandrouters.types.CommandRouteNotFoundException;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Vetoed;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Juraji on 1-5-2017.
 * Biliomi v3
 */
@Vetoed
@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
public class SubCommandRouter {
  private final Component parentComponent;
  private final Table<String, String, Method> subCommandExecutors = HashBasedTable.create();

  public SubCommandRouter(Component parentComponent) {
    this.parentComponent = parentComponent;
  }

  public void buildRoutes() throws Exception {
    if (subCommandExecutors.isEmpty()) {
      CommandRouter.findCommandMethods(parentComponent.getClass(), SubCommandRoute.class)
          .forEach((annot, method) -> subCommandExecutors.put(annot.parentCommand(), annot.command(), method));
    }
  }

  public boolean invokeSubCommand(String parentCommand, String subCommand, User user, Arguments arguments) throws CommandRouteInvocationException {
    Method method = subCommandExecutors.get(parentCommand, subCommand);

    if (method == null) {
      throw new CommandRouteNotFoundException("No executor exists for subcommand " + parentCommand + " -> " + subCommand);
    }

    try {
      return (boolean) method.invoke(parentComponent, user, arguments);
    } catch (IllegalAccessException e) {
      throw new CommandRouteInvocationException("Executor not accessible for subcommand " + parentCommand + " -> " + subCommand, e);
    } catch (InvocationTargetException e) {
      throw new CommandRouteInvocationException("Error invoking executor for subcommand " + parentCommand + " -> " + subCommand, e);
    }
  }
}
