package nl.juraji.biliomi.io.api.twitch.irc;

import nl.juraji.biliomi.io.web.sockets.SocketClient;
import nl.juraji.biliomi.io.web.sockets.SocketSession;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.BotName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.ChannelName;
import nl.juraji.biliomi.utility.events.EventBus;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 5-9-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class IrcSession extends SocketSession {
    public static final String SYSTEM_USER = "jtv";

    @Inject
    private EventBus eventBus;

    @Inject
    private AuthTokenDao authTokenDao;

    @Inject
    @BotName
    private String botName;

    @Inject
    @ChannelName
    private String channelName;

    @Override
    protected SocketClient produceSocketClient() {
        AuthToken botToken = authTokenDao.get(TokenGroup.TWITCH, "bot");
        return new BotChatClient(eventBus, channelName, botName, botToken.getToken());
    }

    @Override
    protected String produceSocketUri() {
        return "wss://irc-ws.chat.twitch.tv";
    }

    public ChatClientFacade getChatClient() {
        return (ChatClientFacade) getSocketClient();
    }
}
