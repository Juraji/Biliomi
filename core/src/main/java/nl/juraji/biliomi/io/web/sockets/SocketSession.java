package nl.juraji.biliomi.io.web.sockets;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import nl.juraji.biliomi.io.web.SslOverTlsContextFactory;
import nl.juraji.biliomi.utility.types.Restartable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * Created by Juraji on 3-10-2017.
 * Biliomi
 */
public abstract class SocketSession implements Restartable {

  protected final Logger logger;
  private final WebSocketFactory webSocketFactory;
  private WebSocket clientWebSocket;
  private SocketClient socketClient;

  public SocketSession() {
    webSocketFactory = new WebSocketFactory()
        .setConnectionTimeout(SocketClient.CONNECTION_TIMEOUT)
        .setSSLContext(new SslOverTlsContextFactory().getSslContext());
    logger = LogManager.getLogger(getClass());
  }

  @Override
  public void start() {
    try {
      if (socketClient == null) {
        socketClient = produceSocketClient();
      }

      if (clientWebSocket == null) {
        clientWebSocket = webSocketFactory.createSocket(produceSocketUri())
            .addListener(socketClient)
            .connect();
      } else if (!clientWebSocket.isOpen()) {
        clientWebSocket = clientWebSocket.recreate().connect();
      }
    } catch (IOException | WebSocketException e) {
      logger.error("Failed constructing websocket", e);
    }
  }

  @Override
  @PreDestroy
  public void stop() {
    if (clientWebSocket != null && clientWebSocket.isOpen()) {
      clientWebSocket.disconnect();
    }
  }

  public SocketClient getSocketClient() {
    return socketClient;
  }

  protected abstract SocketClient produceSocketClient();

  protected abstract String produceSocketUri();
}
