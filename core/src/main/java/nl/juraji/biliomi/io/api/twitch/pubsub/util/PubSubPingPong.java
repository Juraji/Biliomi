package nl.juraji.biliomi.io.api.twitch.pubsub.util;

import com.neovisionaries.ws.client.WebSocket;
import nl.juraji.biliomi.io.api.twitch.pubsub.model.types.PubSubHeartBeatMessage;
import nl.juraji.biliomi.utility.factories.concurrent.ThreadPools;
import nl.juraji.biliomi.utility.types.Restartable;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by Juraji on 5-9-2017.
 * Biliomi v3
 */
public class PubSubPingPong implements Restartable {
  private final ScheduledExecutorService executor;

  private Consumer<WebSocket> onPongTimeout;
  private WebSocket webSocket;
  private ScheduledFuture<?> pingRunnerfuture;
  private ScheduledFuture<?> pongTimeOutFuture;

  public PubSubPingPong() {
    this.executor = ThreadPools.newScheduledExecutorService(1, getClass().getSimpleName());
  }

  /**
   * Will start sending PING messages to the pubsub server
   * Be sure to call pongReceived() when a pong message is received to cancel the timeout
   */
  @Override
  public void start() {
    if (webSocket == null) {
      throw new IllegalStateException("Cannot ping to NULL websocket");
    }

    if (pingRunnerfuture == null) {
      pingRunnerfuture = executor.scheduleAtFixedRate(this::ping, 1, 4, TimeUnit.MINUTES);
    }
  }

  /**
   * Stops sending PING messages
   */
  @Override
  public void stop() {
    if (pingRunnerfuture != null) {
      pingRunnerfuture.cancel(true);
      pingRunnerfuture = null;
    }
  }

  public void setWebSocket(WebSocket webSocket) {
    this.webSocket = webSocket;
  }

  public void setOnPongTimeout(Consumer<WebSocket> onPongTimeout) {
    this.onPongTimeout = onPongTimeout;
  }

  /**
   * Should be called when a PONG message is received.
   * This cancels the timeout
   */
  public void pongReceived() {
    if (pongTimeOutFuture != null) {
      pongTimeOutFuture.cancel(false);
      pongTimeOutFuture = null;
    }
  }

  private void ping() {
    webSocket.sendText(PubSubHeartBeatMessage.PING);
    if (onPongTimeout != null) {
      pongTimeOutFuture = executor.schedule(() -> onPongTimeout.accept(webSocket), 10, TimeUnit.SECONDS);
    }
  }
}
