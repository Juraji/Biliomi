package nl.juraji.biliomi.io.api.twitch.irc.utils;

import com.neovisionaries.ws.client.WebSocket;
import nl.juraji.biliomi.utility.calculate.TextUtils;
import nl.juraji.biliomi.utility.factories.concurrent.ThreadPools;
import nl.juraji.biliomi.utility.types.Restartable;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.collections.FastList;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class MessageQueue implements Restartable {
  // As moderator Biliomi is allowed to send a total of 100 messages per 30s
  // To stay on the safe side, with timed execution drifting in mind, this is reduced to 80 messages per 30s
  private static final int SEND_PERIOD = 375;
  private static final String MSG_PREFIX_PATTERN = "PRIVMSG #{{username}} :";
  // De internet says 500, so keeping margin of error
  private static final int IRC_MAX_MESSAGE_LENGTH = 450;

  private final FastList<String> queue = new FastList<>();
  private final String prefix;

  private WebSocket webSocket;

  private ScheduledExecutorService executor;

  public MessageQueue(String username) {
    prefix = Templater.template(MSG_PREFIX_PATTERN).add("username", username).apply();
  }

  @Override
  public void start() {
    queue.clear();
    if (webSocket != null) {
      executor = ThreadPools.newScheduledExecutorService("MessageQueue");
      executor.scheduleAtFixedRate(this::sendMessages, 0, SEND_PERIOD, TimeUnit.MILLISECONDS);
    }
  }

  @Override
  public void stop() {
    if (executor != null) {
      executor.shutdown();
      executor = null;
    }
  }

  public void setWebSocket(WebSocket webSocket) {
    this.webSocket = webSocket;
  }

  public void addMessage(String message) {
    if (message.length() > IRC_MAX_MESSAGE_LENGTH) {
      List<String> chunks = TextUtils.chunkify(message, IRC_MAX_MESSAGE_LENGTH);
      queue.addAll(chunks);
    } else {
      queue.add(message);
    }
  }

  private void sendMessages() {
    if (!queue.isEmpty() && webSocket.isOpen()) {
      String message = queue.pollFirst();
      if (StringUtils.isNotEmpty(message)) {
        webSocket.sendText(prefix + message);
      }
    }
  }
}
