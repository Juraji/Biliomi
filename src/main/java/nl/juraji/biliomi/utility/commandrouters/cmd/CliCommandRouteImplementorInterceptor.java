package nl.juraji.biliomi.utility.commandrouters.cmd;

import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.utility.commandrouters.CommandExecutorUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * Created by Juraji on 7-5-2017.
 * Biliomi v3
 */
@Interceptor
@CliCommandRouteImplementor
public class CliCommandRouteImplementorInterceptor {

  @Inject
  private CliCommandRouterRegistry cliCommandRouterRegistry;

  @PostConstruct
  public Object registerCommandRoutes(InvocationContext ctx) throws Exception {
    Component component = (Component) ctx.getTarget();
    CommandExecutorUtils.findCliCommandExecutors(component.getClass())
        .mapKey(CliCommandRoute::command)
        .forEach((command, method) -> cliCommandRouterRegistry.put(command, component, method));

    return ctx.proceed();
  }
}
