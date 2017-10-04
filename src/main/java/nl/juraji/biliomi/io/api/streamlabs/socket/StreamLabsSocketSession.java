package nl.juraji.biliomi.io.api.streamlabs.socket;

import io.socket.client.IO;
import io.socket.client.Socket;
import nl.juraji.biliomi.io.api.streamlabs.socket.listeners.StreamLabsEventListener;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppDataValue;
import nl.juraji.biliomi.utility.events.EventBus;
import nl.juraji.biliomi.utility.factories.ModelUtils;
import nl.juraji.biliomi.utility.types.Restartable;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
@Default
@Singleton
public class StreamLabsSocketSession implements Restartable {

  @Inject
  private Logger logger;

  @Inject
  private EventBus eventBus;

  @Inject
  @AppDataValue("streamlabs.api.uris.socket")
  private String socketUri;

  private String socketToken;
  private Socket socket;

  @Override
  public void start() {
    if (socketToken == null) {
      return;
    }

    if (socket == null) {
      try {
        socket = IO.socket(socketUri, getIOOptions());
        socket.on("event", new StreamLabsEventListener(eventBus));
      } catch (URISyntaxException e) {
        logger.error("Failed connection to Stream Labs socket.io", e);
      }
    } else {
      stop();
    }

    socket.connect();
  }

  @Override
  public void stop() {
    if (socket != null && socket.connected()) {
      socket.disconnect();
    }
  }

  public void setSocketToken(String socketToken) {
    this.socketToken = socketToken;
  }

  private IO.Options getIOOptions() {
    IO.Options options = new IO.Options();

    options.query = "token=" + socketToken;
    options.reconnection = true;
    options.reconnectionAttempts = 3;
    options.reconnectionDelay = 10000;
    options.reconnectionDelayMax = 60000;

    return options;
  }
}
