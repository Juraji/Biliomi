package nl.juraji.biliomi.components.shared;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.io.api.twitch.irc.IrcSession;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.UserDao;
import nl.juraji.biliomi.model.core.settings.SystemSettings;
import nl.juraji.biliomi.model.internal.events.irc.user.messages.IrcChatMessageEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.messages.IrcPrivateMessageEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.messages.IrcSystemMessageEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.state.IrcUserJoinedEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.state.IrcUserLeftEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.state.IrcUserModeEvent;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.collections.FastList;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 23-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
@EventBusSubscriber
public class ChatService {
    public static final String TIMEOUT_PREFIX = ".timeout";
    public static final String BAN_PREFIX = ".ban";
    public static final String UNBAN_PREFIX = ".unban";
    private static final TimeUnit TIMEOUT_TUNIT = TimeUnit.SECONDS;
    private final List<String> viewers = new FastList<>();
    private SystemSettings systemSettings;
    @Inject
    private Logger logger;
    @Inject
    private IrcSession session;
    @Inject
    private UsersService usersService;
    @Inject
    private UserDao userDao;
    @Inject
    private SettingsService settingsService;

    @PostConstruct
    private void initChatService() {
        systemSettings = settingsService.getSettings(SystemSettings.class, s -> systemSettings = s);
    }

    @Subscribe
    public void onIrcUserJoinedEvent(IrcUserJoinedEvent event) {
        if (!viewers.contains(event.getUsername())) {
            // Since the part event is not always emitted we need to check
            // if we don't already know this user is present
            logger.info("[JOIN] {}", event.getUsername());
            viewers.add(event.getUsername());
        }
    }

    @Subscribe
    public void onIrcUserLeftEvent(IrcUserLeftEvent event) {
        logger.info("[PART] {}", event.getUsername());
        viewers.remove(event.getUsername());
    }

    @Subscribe
    public void onIrcUserModeEvent(IrcUserModeEvent event) {
        if (event.isModeratorMode()) {
            User user = usersService.getUser(event.getUsername());
            if (!user.isModerator()) {
                user.setModerator(true);
                userDao.save(user);
            }
        }
    }

    @Subscribe
    public void onIrcChatMessageEvent(IrcChatMessageEvent event) {
        logger.info("[MSG] [CHAT] {}: {}", event.getUsername(), event.getMessage());
    }

    @Subscribe
    public void onIrcPrivateMessageEvent(IrcPrivateMessageEvent event) {
        logger.info("[MSG] [PM] {}: {}", event.getUsername(), event.getMessage());
    }

    @Subscribe
    public void onIrcSystemMessageEvent(IrcSystemMessageEvent event) {
        logger.info("[MSG] [SYS] {}: {}", event.getUsername(), event.getMessage());
    }

    public List<String> getViewers() {
        return viewers;
    }

    public List<User> getViewersAsUsers() {
        return viewers.stream()
                .map(username -> usersService.getUser(username))
                .collect(Collectors.toList());
    }

    public void say(String message) {
        if (systemSettings.isMuted()) {
            logger.info("[SAY MUTED] {}", message);
        } else {
            session.getChatClient().say(message);
            logger.info("[SAY] {}", message);
        }
    }

    public void say(Templater template) {
        say(template.apply());
    }

    public void whisper(String username, String message) {
        if (systemSettings.isMuted()) {
            logger.info("[@{} MUTED] {}", username, message);
        } else {
            if (systemSettings.isEnableWhispers()) {
                session.getChatClient().whisper(username, message);
            } else {
                String atMessage = Templater.template("@{{wtargetusername}}, {{wmessage}}")
                        .add("wtargetusername", username)
                        .add("wmessage", message)
                        .apply();
                session.getChatClient().say(atMessage);
            }

            logger.info("[@{}] {}", username, message);
        }
    }

    public void whisper(String username, Templater message) {
        whisper(username, message.apply());
    }

    public void whisper(User user, String message) {
        whisper(user.getUsername(), message);
    }

    public void whisper(User user, Templater message) {
        whisper(user.getUsername(), message);
    }

    public void purgeUser(String username) {
        session.getChatClient().say(Templater.spaced(TIMEOUT_PREFIX, username, "1"));
    }

    public void timeoutUser(String username) {
        session.getChatClient().say(Templater.spaced(TIMEOUT_PREFIX, username));
    }

    public void timeoutUser(String username, long timeoutMillis) {
        session.getChatClient().say(Templater.spaced(TIMEOUT_PREFIX, username, TIMEOUT_TUNIT.convert(timeoutMillis, TimeUnit.MILLISECONDS)));
    }

    public void banUser(String username) {
        session.getChatClient().say(Templater.spaced(BAN_PREFIX, username));
    }

    public void unbanUser(String username) {
        session.getChatClient().say(Templater.spaced(UNBAN_PREFIX, username));
    }
}
