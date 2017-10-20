package nl.juraji.biliomi.io.api.twitch.pubsub;

import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.io.web.sockets.SocketClient;
import nl.juraji.biliomi.io.web.sockets.SocketSession;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.events.EventBus;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 3-9-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class PubSubSession extends SocketSession {

  @Inject
  private EventBus eventBus;

  @Inject
  private ChannelService channelService;

  @Inject
  private AuthTokenDao authTokenDao;

  @Override
  protected SocketClient produceSocketClient() {
    long channelId = channelService.getChannelId();
    AuthToken channelToken = authTokenDao.get(TokenGroup.TWITCH, "channel");
    return new PubSubClient(eventBus, channelId, channelToken.getToken());
  }

  @Override
  protected String produceSocketUri() {
    return "wss://pubsub-edge.twitch.tv";
  }
}
