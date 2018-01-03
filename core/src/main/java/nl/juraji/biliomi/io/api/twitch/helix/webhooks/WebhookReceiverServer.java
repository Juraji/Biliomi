package nl.juraji.biliomi.io.api.twitch.helix.webhooks;

import fi.iki.elonen.NanoHTTPD;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.utility.factories.concurrent.ThreadPools;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Created by Juraji on 3-1-2018.
 * Biliomi
 */
public class WebhookReceiverServer extends NanoHTTPD {

  private final Logger logger;
  private final Consumer<IHTTPSession> onInput;
  private ExecutorService onInputExecutor;

  public WebhookReceiverServer(int port, Consumer<IHTTPSession> onInput) {
    super(port);
    this.logger = LogManager.getLogger(getClass().getSimpleName());
    this.onInput = onInput;
  }

  @Override
  public Response serve(IHTTPSession session) {
    String response = null;
    String queryParameterString = session.getQueryParameterString();

    if (StringUtils.isNotEmpty(queryParameterString)) {
      Map<String, String> hubQuery = Url.unpackQueryString(queryParameterString, true);

      String hubMode = hubQuery.get("hub.mode");
      if ("denied".equals(hubMode)) {
        // A topic subscription was denied,
        logger.error("Failed to subscribe to topic: {} ({})", hubQuery.get("hub.topic"), hubQuery.get("hub.reason"));
      } else if ("subscribe".equals(hubMode)) {
        logger.debug("Hub challenge received for topic: {}", hubQuery.get("hub.topic"));
        response = hubQuery.get("hub.challenge");
      }
    } else {
      onInputExecutor.submit(() -> onInput.accept(session));
    }

    // Always return a response
    return newFixedLengthResponse(response);
  }

  @Override
  public void start(int timeout, boolean daemon) throws IOException {
    super.start(timeout, daemon);
    this.onInputExecutor = ThreadPools.newExecutorService(4, "WebhookReceiverServerDataThread");
  }

  @Override
  public void stop() {
    super.stop();

    if (this.onInputExecutor != null) {
      this.onInputExecutor.shutdownNow();
      this.onInputExecutor = null;
    }
  }
}
