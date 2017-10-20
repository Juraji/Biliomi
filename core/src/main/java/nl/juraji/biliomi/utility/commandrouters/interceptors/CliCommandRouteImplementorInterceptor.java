package nl.juraji.biliomi.utility.commandrouters.interceptors;

import nl.juraji.biliomi.utility.commandrouters.annotations.CliCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.CliCommandRouteImplementor;
import nl.juraji.biliomi.utility.commandrouters.routers.CliCommandRouterRegistry;
import nl.juraji.biliomi.utility.commandrouters.routers.CommandRouter;
import nl.juraji.biliomi.utility.types.components.Component;

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
    CommandRouter.findCommandMethods(component.getClass(), CliCommandRoute.class)
        .mapKey(CliCommandRoute::command)
        .forEach((command, method) -> cliCommandRouterRegistry.put(command, component, method));

    return ctx.proceed();
  }
}
