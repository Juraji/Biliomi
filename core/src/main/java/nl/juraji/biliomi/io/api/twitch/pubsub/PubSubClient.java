package nl.juraji.biliomi.io.api.twitch.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import nl.juraji.biliomi.io.api.twitch.pubsub.model.commands.PubSubListenCommand;
import nl.juraji.biliomi.io.api.twitch.pubsub.model.commands.PubSubListenCommandData;
import nl.juraji.biliomi.io.api.twitch.pubsub.model.message.PubSubMessage;
import nl.juraji.biliomi.io.api.twitch.pubsub.model.message.PubSubMessageData;
import nl.juraji.biliomi.io.api.twitch.pubsub.model.types.PubSubErrorType;
import nl.juraji.biliomi.io.api.twitch.pubsub.model.types.PubSubHeartBeatMessage;
import nl.juraji.biliomi.io.api.twitch.pubsub.util.PubSubPingPong;
import nl.juraji.biliomi.io.api.twitch.pubsub.util.TopicDataToEventMapper;
import nl.juraji.biliomi.io.web.sockets.SocketClient;
import nl.juraji.biliomi.model.internal.events.twitch.TwitchEvent;
import nl.juraji.biliomi.utility.events.EventBus;
import nl.juraji.biliomi.utility.types.TokenGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static nl.juraji.biliomi.io.api.twitch.pubsub.model.types.PubSubTopic.BITS;
import static nl.juraji.biliomi.io.api.twitch.pubsub.model.types.PubSubTopic.SUBSCRIPTIONS;
import static nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller.marshal;
import static nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller.unmarshal;

/**
 * Created by Juraji on 5-9-2017.
 * Biliomi v3
 */
public class PubSubClient extends SocketClient {
  private final PubSubPingPong pingPong;
  private final EventBus eventBus;
  private final long channelId;
  private final String oAuth;
  private final String oAuthState;

  public PubSubClient(EventBus eventBus, long channelId, String oAuth) {
    this.pingPong = new PubSubPingPong();
    this.eventBus = eventBus;
    this.channelId = channelId;
    this.oAuth = oAuth;

    this.pingPong.setOnPongTimeout(this::reconnect);
    oAuthState = new TokenGenerator(10, false).generate();
  }

  @Override
  public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
    logger.debug("Connected to Twitch PubSub, requesting topics...");

    PubSubListenCommand command = new PubSubListenCommand();
    PubSubListenCommandData data = new PubSubListenCommandData();

    command.setNonce(oAuthState);

    data.setAuthToken(oAuth);
    data.addTopic(BITS, channelId);
    data.addTopic(SUBSCRIPTIONS, channelId);

    command.setData(data);
    String marshal = marshal(command);
    websocket.sendText(marshal);
  }

  @Override
  public void onTextMessage(WebSocket websocket, String text) throws Exception {
    PubSubMessage pubSubMessage = unmarshal(text, PubSubMessage.class);

    switch (pubSubMessage.getType()) {
      case PING:
        websocket.sendText(PubSubHeartBeatMessage.PONG);
        break;
      case PONG:
        this.pingPong.pongReceived();
        break;
      case RECONNECT:
        break;
      case RESPONSE:
        handleResponseMessage(websocket, pubSubMessage.getError(), pubSubMessage.getNonce());
        break;
      case MESSAGE:
        handleMessageData(pubSubMessage.getData());
        break;
    }
  }

  private void handleResponseMessage(WebSocket websocket, PubSubErrorType error, String nonce) throws JsonProcessingException {
    if (!nonce.equals(oAuthState)) {
      logger.error("The authentication nonce did not match. Twitch PubSub might be compromised, aborting connection");
      websocket.disconnect("Incorrect nonce");
      return;
    }

    if (error == null) {
      logger.info("Successfully subscribed to Twitch PubSub");
      pingPong.setWebSocket(websocket);
      pingPong.start();
    } else {
      logger.error("PubSub service would not accept topics due to error " + error.toString());
    }
  }

  private void handleMessageData(String data) throws IOException {
    PubSubMessageData messageData = unmarshal(data, PubSubMessageData.class);
    TwitchEvent event = null;

    switch (messageData.getTopic()) {
      case BITS:
        event = TopicDataToEventMapper.bits(messageData.getMessage());
        break;
      case SUBSCRIPTIONS:
        event = TopicDataToEventMapper.subscription(messageData.getMessage());
        break;
      case COMMERCE:
        break;
      case WHISPERS:
        break;
    }

    if (event != null) {
      eventBus.post(event);
    }
  }

  private void reconnect(WebSocket webSocket) {
    try {
      Thread.sleep(CONNECTION_TIMEOUT);
      webSocket.recreate().connect();
    } catch (WebSocketException | IOException | InterruptedException e) {
      logger.error("Failed reconnecting websocket", e);
      this.pingPong.stop();
    }
  }
}
