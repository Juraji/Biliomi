package nl.juraji.biliomi.components.system.cli;

import nl.juraji.biliomi.components.ComponentManager;
import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.model.core.Command;
import nl.juraji.biliomi.model.core.CommandDao;
import nl.juraji.biliomi.utility.calculate.TextUtils;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.routers.CommandRouter;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.factories.reflections.ReflectionUtils;
import nl.juraji.biliomi.utility.types.Exporter;
import nl.juraji.biliomi.utility.types.collections.FastList;
import nl.juraji.biliomi.utility.types.components.Component;
import org.apache.commons.beanutils.BeanUtils;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Created by Juraji on 8-5-2017.
 * Biliomi v3
 */
@Default
public class SystemCommandExporter extends Exporter {

    @Inject
    private CommandDao commandDao;

    @Inject
    private TimeFormatter timeFormatter;

    @Inject
    private PointsService pointsService;

    public SystemCommandExporter() throws IOException {
        super("Command", "System Command", "Moderator Can Always Activate", "User Group", "Cooldown", "Price", "Aliasses");
    }

    @Override
    public void generateRows() {
        List<Command> commands = listSystemCommands();

        EStream.from(commands)
                .sorted((c1, c2) -> c1.getCommand().compareTo(c2.getCommand()))
                .forEach(command -> addRecord(
                        command.getCommand(),
                        command.isSystemCommand(),
                        command.isModeratorCanActivate(),
                        (command.isSystemCommand() ? null : command.getUserGroup().getName()),
                        (command.getCooldown() == 0 ? null : timeFormatter.timeQuantity(command.getCooldown())),
                        (command.getPrice() == 0 ? null : pointsService.asString(command.getPrice())),
                        TextUtils.commaList(command.getAliasses())
                ));
    }

    private List<Command> listSystemCommands() {
        List<Command> commands = new FastList<>();
        List<CommandRoute> commandRoutes = new FastList<>();
        List<SubCommandRoute> subCommandRoutes = new FastList<>();

        // Gather all commandroutes and subCommandRoutes
        ReflectionUtils.forClassPackage(ComponentManager.class)
                .subTypes(Component.class)
                .forEach(componentClass -> {
                    CommandRouter.findCommandMethods(componentClass, CommandRoute.class)
                            .map((route, method) -> route)
                            .forEach(commandRoutes::add);
                    CommandRouter.findCommandMethods(componentClass, SubCommandRoute.class)
                            .map((subRoute, method) -> subRoute)
                            .forEach(subCommandRoutes::add);
                });

        // Build commands list
        commandRoutes.stream()
                .map(commandRoute -> commandDao.getCommand(commandRoute.command()))
                .forEach(command -> {
                    // Add this command
                    commands.add(command);
                    // Add all subroutes
                    EStream.from(subCommandRoutes)
                            .filter(subCommandRoute -> command.getCommand().equals(subCommandRoute.parentCommand()))
                            .map(subCommandRoute -> {
                                // Clone the parent command obj, add subroute to command and return clone
                                Command clone = (Command) BeanUtils.cloneBean(command);
                                clone.setCommand(clone.getCommand() + ' ' + subCommandRoute.command());
                                return clone;
                            })
                            .forEach(commands::add);
                });

        return commands;
    }

    @Override
    public String getDoneMessage() {
        return "Saved an export of all system commands to \"" + getTargetFile().getAbsolutePath() + "\"";
    }
}
