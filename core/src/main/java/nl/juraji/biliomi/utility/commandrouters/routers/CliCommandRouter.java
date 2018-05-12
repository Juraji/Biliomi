package nl.juraji.biliomi.utility.commandrouters.routers;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.messages.IrcChatMessageEvent;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.ChannelName;
import nl.juraji.biliomi.utility.commandrouters.types.RegistryEntry;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

/**
 * Created by robin on 1-6-17.
 * biliomi
 */
@Default
@Singleton
@EventBusSubscriber
public class CliCommandRouter {
    public static final char PREFIX_COMMAND = '!';
    public static final char PREFIX_CMD_COMMAND = '/';
    public static final char PREFIX_WHISPER = '@';
    public static final char PREFIX_SAY = '>';
    @Inject
    protected ChatService chat;
    @Inject
    private Logger logger;
    @Inject
    private CommandRouter commandRouter;

    @Inject
    private CliCommandRouterRegistry cmdCommandRegistry;

    @Inject
    @ChannelName
    private String channelName;

    @Subscribe
    public void onConsoleInputEvent(ConsoleInputEvent event) {
        char identifier = event.getIdentifier();

        switch (identifier) {
            case PREFIX_COMMAND:
                runCommand(event);
                break;
            case PREFIX_CMD_COMMAND:
                executeConsoleCommand(event);
                break;
            case PREFIX_WHISPER:
                doWhisper(event);
                break;
            case PREFIX_SAY:
                doSay(event);
                break;
            default:
                break;
        }
    }

    private void doSay(ConsoleInputEvent event) {
        chat.say(event.getInput());
    }

    private void doWhisper(ConsoleInputEvent event) {
        LinkedList<String> list = new LinkedList<>(event.getInputSplit());

        if (list.isEmpty()) {
            return;
        }

        if (list.size() < 2) {
            logger.error("Whispering empty messages is not allowed");
            return;
        }

        String username = list.pollFirst();
        String message = list.stream().reduce((l, r) -> l + r).orElse("");
        chat.whisper(username, message);
    }

    private void runCommand(ConsoleInputEvent event) {
        logger.info("Executing chat command: {}", event.toString());
        IrcChatMessageEvent chatEvent = new IrcChatMessageEvent(channelName, null, event.toString());
        commandRouter.runCommand(chatEvent, false);
    }

    private void executeConsoleCommand(ConsoleInputEvent event) {
        LinkedList<String> list = new LinkedList<>(event.getInputSplit());

        if (list.isEmpty()) {
            logger.info("Usage: /help");
            return;
        }

        RegistryEntry registryEntry = cmdCommandRegistry.get(list.pollFirst());
        if (registryEntry == null) {
            logger.info("Usage: /help");
            return;
        }

        try {
            registryEntry.getMethod().invoke(registryEntry.getComponentInstance(), event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("Failed invoking command executor for input /" + event.toString(), e);
        }
    }

}
