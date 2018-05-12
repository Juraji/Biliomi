package nl.juraji.biliomi.utility.commandrouters.interceptors;

import nl.juraji.biliomi.components.system.commands.CommandService;
import nl.juraji.biliomi.components.system.users.UserGroupService;
import nl.juraji.biliomi.model.core.Command;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRouteImplementor;
import nl.juraji.biliomi.utility.commandrouters.routers.CommandRouter;
import nl.juraji.biliomi.utility.commandrouters.routers.CommandRouterRegistry;
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
@CommandRouteImplementor
public class CommandRouteImplementorInterceptor {

    @Inject
    private CommandService commandService;

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private CommandRouterRegistry commandRouterRegistry;

    @PostConstruct
    public Object registerCommandRoutes(InvocationContext ctx) throws Exception {
        Component component = (Component) ctx.getTarget();
        CommandRouter.findCommandMethods(component.getClass(), CommandRoute.class)
                .mapKey(this::persistedCommand)
                .forEach((command, method) -> {
                    // Register the command
                    commandRouterRegistry.put(command.getCommand(), component, method);
                    // Register any aliasses
                    command.getAliasses().forEach(alias -> commandRouterRegistry.putAlias(alias, command.getCommand()));
                });

        return ctx.proceed();
    }

    private Command persistedCommand(CommandRoute properties) {
        Command command = commandService.getCommand(properties.command());

        if (command == null) {
            command = new Command();
            command.setCommand(properties.command());
            command.setSystemCommand(properties.systemCommand());
            command.setModeratorCanActivate(properties.modCanActivate());
            command.setCooldown(properties.defaultCooldown());
            command.setPrice(properties.defaultPrice());
            command.setUserGroup(userGroupService.getDefaultGroup());
            commandService.save(command);
        }

        return command;
    }
}
