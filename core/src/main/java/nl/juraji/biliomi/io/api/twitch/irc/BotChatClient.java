package nl.juraji.biliomi.io.api.twitch.irc;

import com.neovisionaries.ws.client.WebSocket;
import nl.juraji.biliomi.io.api.twitch.irc.utils.IrcCommand;
import nl.juraji.biliomi.io.api.twitch.irc.utils.MessageQueue;
import nl.juraji.biliomi.io.api.twitch.irc.utils.MessageReader;
import nl.juraji.biliomi.io.api.twitch.irc.utils.Tags;
import nl.juraji.biliomi.io.web.sockets.SocketClient;
import nl.juraji.biliomi.model.internal.events.irc.channel.IrcChannelJoinedEvent;
import nl.juraji.biliomi.model.internal.events.irc.channel.IrcChannelNoticeEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.messages.IrcChatMessageEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.messages.IrcPrivateMessageEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.messages.IrcSystemMessageEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.state.IrcUserJoinedEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.state.IrcUserLeftEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.state.IrcUserModeEvent;
import nl.juraji.biliomi.model.internal.events.twitch.bits.TwitchBitsEvent;
import nl.juraji.biliomi.utility.calculate.NumberConverter;
import nl.juraji.biliomi.utility.events.EventBus;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static nl.juraji.biliomi.io.api.twitch.irc.IrcSession.SYSTEM_USER;

/**
 * Created by Juraji on 5-9-2017.
 * Biliomi v3
 */
public class BotChatClient extends SocketClient implements ChatClientFacade {
  private static final String MSG_LOGIN_ERROR = "Error logging in";
  private static final String MSG_AUTH_FAIL = "Login authentication failed";
  private static final String MSG_INVALID_NICK = "Invalid NICK";

  private final EventBus eventBus;
  private final String oAuth;
  private final String username;

  private final String channelName;
  private final MessageQueue messageQueue;

  public BotChatClient(EventBus eventBus, String channelName, String username, String oAuth) {
    this.eventBus = eventBus;
    this.username = username;
    this.oAuth = oAuth;
    this.channelName = channelName;
    this.messageQueue = new MessageQueue(channelName);
  }

  @Override
  public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
    // Request capabilities
    websocket.sendText("CAP REQ :twitch.tv/membership");
    websocket.sendText("CAP REQ :twitch.tv/commands");
    websocket.sendText("CAP REQ :twitch.tv/tags");

    // Log in
    websocket.sendText("PASS oauth:" + oAuth);
    websocket.sendText("NICK " + username);
    messageQueue.setWebSocket(websocket);
  }

  @Override
  public void onTextMessage(WebSocket websocket, String text) {
    // Catch server emitting PING, respond and break
    if (text.contains(IrcCommand.PING.toString())) {
      websocket.sendText(IrcCommand.PONG.toString());
      return;
    }

    // Split multiline mesages and handle each as seperate message
    String[] msgs = text.trim().split("\r\n");
    Arrays.stream(msgs)
        .filter(msg -> msg.indexOf(':') == 0 || msg.indexOf('@') == 0)
        .forEach(s -> this.parseMessage(websocket, s));
  }

  // LOGIN succeeded
  protected void on001(WebSocket webSocket) {
    webSocket.sendText("JOIN #" + channelName);
    logger.info("Joined channel #{}", channelName);
    messageQueue.start();
    eventBus.post(new IrcChannelJoinedEvent(channelName, username));
  }

  // Incoming message
  protected void onPRIVMSG(String message, String username, Tags tags) {
    // User is using /me
    if (message.startsWith("\001ACTION")) {
      message = message.replace("\001ACTION", "/me");
    }

    // Emit user mode (for updating user is moderator or not)
    if (Tags.UserType.MOD.equals(tags.getUserType())) {
      eventBus.post(new IrcUserModeEvent(username, tags, true));
    }

    // Emit bits if present
    String bits = tags.getBits();
    if (StringUtils.isNotEmpty(bits)) {
      Long longBits = NumberConverter.asNumber(bits).withDefault(0).toLong();
      eventBus.post(new TwitchBitsEvent(username, longBits));
    }

    eventBus.post(new IrcChatMessageEvent(username, tags, message));
  }

  // incoming whisper
  protected void onWHISPER(String message, String username, Tags tags) {
    if (SYSTEM_USER.equals(username)) {
      eventBus.post(new IrcSystemMessageEvent(SYSTEM_USER, tags, message));
    } else {
      eventBus.post(new IrcPrivateMessageEvent(username, tags, message));
    }
  }

  // User joined
  protected void onJOIN(String username, Tags tags) {
    eventBus.post(new IrcUserJoinedEvent(username, tags));
  }

  // Incoming notice
  protected void onNOTICE(WebSocket webSocket, String message, Tags tags) {
    switch (message) {
      case MSG_INVALID_NICK:
      case MSG_LOGIN_ERROR:
        logger.error("Error logging in , check the bot username");
        webSocket.disconnect();
        break;
      case MSG_AUTH_FAIL:
        logger.error("Failed authenticating, check the oAuth token for {}", username);
        webSocket.disconnect();
        break;
      default:
        break;
    }

    eventBus.post(new IrcChannelNoticeEvent(channelName, tags, message));
  }

  protected void onPART(String username) {
    eventBus.post(new IrcUserLeftEvent(username, null));
  }

  @Override
  public void say(String message) {
    messageQueue.addMessage(message);
  }

  @Override
  public void whisper(String username, String message) {
    say("/w " + username + " " + message);
  }

  private void parseMessage(WebSocket webSocket, String s) {
    MessageReader m = new MessageReader(s);
    IrcCommand command = m.getIrcCommand();

    if (command == null) {
      return;
    }

    // Not all commands are implemented, most are ignored
    switch (command) {
      case C001:
        on001(webSocket);
        break;
      case PRIVMSG:
        onPRIVMSG(m.getMessage(), m.getUsername(), m.getTags());
        break;
      case WHISPER:
        onWHISPER(m.getMessage(), m.getUsername(), m.getTags());
        break;
      case JOIN:
        onJOIN(m.getUsername(), m.getTags());
        break;
      case PART:
        onPART(m.getUsername());
        break;
      case NOTICE:
        onNOTICE(webSocket, m.getMessage(), m.getTags());
        break;
      default:
        break;
    }
  }
}
