package nl.juraji.biliomi.components.system.cli;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.model.core.VersionInfo;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CliCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.routers.CliCommandRouter;
import nl.juraji.biliomi.utility.commandrouters.routers.CliCommandRouterRegistry;
import nl.juraji.biliomi.utility.estreams.EBiStream;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.types.MutableString;
import nl.juraji.biliomi.utility.types.collections.MultivaluedHashMap;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by robin on 2-6-17.
 * biliomi
 */
@Default
@Singleton
@SystemComponent
public class CliSystemComponent extends Component {

    @Inject
    private CliCommandRouterRegistry cmdCommandRegistry;

    @CliCommandRoute(command = "help", description = "Display usage information about the console")
    public boolean helpCommand(ConsoleInputEvent event) {
        MutableString help = new MutableString();
        MultivaluedHashMap<String, CliCommandRoute> commandDescriptionsMap = cmdCommandRegistry.getCommandDescriptionsMap();

        // Run the version command
        versionCommand(event);

        if (event.isLegacyMode()) {
            help.appendNewLine()
                    .append("The console is running in legacy mode, some features are disabled")
                    .appendNewLine();
        }

        help.appendNewLine()
                .append("Commandline usage:").appendNewLine()
                .append("  Use console: /[command]").appendNewLine()
                .append("  Post commands: ![command]").appendNewLine()
                .append("  Send whispers (using bot account): @[target username] [message...]").appendNewLine()
                .append("  post message in chat (using bot account): >[message...]").appendNewLine()
                .appendNewLine()
                .append("Console Commands:").appendNewLine();

        EBiStream.from(commandDescriptionsMap)
                .forEach((cn, cmds) -> {
                    help.appendSpace(2)
                            .append(cn + ":").appendNewLine();
                    EStream.from(cmds)
                            .mapToBiEStream(CliCommandRoute::command, CliCommandRoute::description)
                            .sortedByKey(String::compareTo)
                            .forEach((c, d) -> help.appendSpace(4)
                                    .append(CliCommandRouter.PREFIX_CMD_COMMAND)
                                    .append(c).append(": ").append(d)
                                    .appendNewLine());
                });

        help.appendNewLine()
                .append("Consult the docs on the console for more information").appendNewLine();

        logger.info(help.toString());
        return true;
    }

    @CliCommandRoute(command = "version", description = "Print current version of Biliomi")
    public boolean versionCommand(ConsoleInputEvent event) {
        CDI<Object> cdi = CDI.current();
        VersionInfo versionInfo = cdi.select(VersionInfo.class).get();
        TimeFormatter timeFormatter = cdi.select(TimeFormatter.class).get();

        logger.info("You are running Biliomi " + versionInfo.getVersion() + ", built on " + timeFormatter.fullDateTime(versionInfo.getBuildDate()));
        return true;
    }

    @CliCommandRoute(command = "exit", description = "Exit Biliomi")
    public boolean exitCommand(ConsoleInputEvent event) {
        BiliomiContainer.getContainer().shutdownNow(0);
        return true;
    }

    @CliCommandRoute(command = "restart", description = "Hot restart Biliomi")
    public boolean restartCommand(ConsoleInputEvent event) {
        if (event.isLegacyMode()) {
            logger.info("Hot restart is not available in legacy console mode");
        } else {
            BiliomiContainer.getContainer().restartNow();
        }
        return true;
    }

    @CliCommandRoute(command = "resetauth", description = "Reset Twitch chat and api authorization")
    public boolean resetAuthCommand(ConsoleInputEvent event) {
        final AuthTokenDao dao = CDI.current().select(AuthTokenDao.class).get();
        final WebClient webClient = CDI.current().select(WebClient.class).get();

        final List<AuthToken> tokens = dao.getAllInGroup(TokenGroup.TWITCH);
        dao.delete(tokens);

        logger.info("Twitch authorization removed for caster and bot");
        BiliomiContainer.getContainer().shutdownNow(0);
        return true;
    }
}
