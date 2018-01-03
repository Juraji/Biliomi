package nl.juraji.biliomi.io.api.twitch.helix.webhooks;

import fi.iki.elonen.NanoHTTPD;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.handlers.NotificationHandler;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.model.WebhookNotification;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.utility.calculate.NumberConverter;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.CoreSetting;
import nl.juraji.biliomi.utility.events.EventBus;
import nl.juraji.biliomi.utility.factories.concurrent.ThreadPools;
import nl.juraji.biliomi.utility.types.Restartable;
import nl.juraji.biliomi.utility.types.collections.TimedList;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
@Default
@Singleton
public class WebhookReceiver implements Restartable {

  private final Map<String, NotificationHandler> notificationHandlers = new HashMap<>();
  private NanoHTTPD httpServer;
  private ExecutorService receiverExecutor;
  private TimedList<String> notificationIdHistory;

  @Inject
  @CoreSetting("biliomi.twitch.webhooks.callbackPort")
  private String webhookCallbackPort;

  @Inject
  private Logger logger;

  @Inject
  private EventBus eventBus;

  @PostConstruct
  private void initWebhookReceiver() {
    int serverPort = NumberConverter.asNumber(webhookCallbackPort).withDefault(30001).toInteger();

    this.httpServer = new NanoHTTPD(serverPort) {
      @Override
      public Response serve(IHTTPSession session) {
        String response = "";

        String queryParameterString = session.getQueryParameterString();
        if (StringUtils.isNotEmpty(queryParameterString)) {
          Map<String, String> query = Url.unpackQueryString(queryParameterString, true);
          if (query.containsKey("hub.challenge")) {
            logger.debug("Hub challenge received for topic: " + query.get("hub.topic"));
            response = query.get("hub.challenge");
          }
        } else {
          receiverExecutor.submit(() -> handleSessionInput(session));
        }

        return newFixedLengthResponse(response);
      }
    };
  }

  @Override
  public void start() {
    try {
      this.notificationIdHistory = new TimedList<>("WebhookReceiverNotificationIdHistory");
      this.receiverExecutor = ThreadPools.newExecutorService(8, "WebhookReceiverNotificationProcessor");
      this.httpServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
      logger.info("Started HTTP server for Twitch webhook updates");
    } catch (IOException e) {
      logger.error("Failed starting HTTP server for Twitch webhook updates", e);
    }
  }

  @Override
  public void stop() {
    this.httpServer.stop();
    if (this.receiverExecutor != null) {
      this.receiverExecutor.shutdownNow();
      this.receiverExecutor = null;
    }

    if (this.notificationIdHistory != null) {
      this.notificationIdHistory.stop();
      this.notificationIdHistory = null;
    }
  }

  public void registerNotificationHandler(String endpoint, NotificationHandler handler) {
    this.notificationHandlers.put(endpoint, handler);
  }

  private void handleSessionInput(NanoHTTPD.IHTTPSession session) {
    if (notificationHandlers.containsKey(session.getUri())) {
      NotificationHandler notificationHandler = notificationHandlers.get(session.getUri());
      String lengthHeader = session.getHeaders().getOrDefault("content-length", null);
      int contentLength = NumberConverter.asNumber(lengthHeader).withDefault(0).toInteger();
      byte[] buffer = new byte[contentLength];

      try {
        session.getInputStream().read(buffer, 0, contentLength);
        String data = new String(buffer).trim();

        if (StringUtils.isNotEmpty(data)) {
          WebhookNotification notification = notificationHandler.unmarshalNotification(data);

          if (!this.notificationIdHistory.contains(notification.getId())) {
            logger.debug("New notification on topic: " + notification.getTopic());
            this.notificationIdHistory.add(notification.getId(), 6, TimeUnit.HOURS);
            //noinspection unchecked Unchecked error can never occur, since the notification is unmarshalled by the handler
            notificationHandler.handleNotification(eventBus, notification);
          }
        }
      } catch (IOException e) {
        logger.error("Failed reading input", e);
      }
    }
  }
}
