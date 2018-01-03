package nl.juraji.biliomi.io.api.twitch.helix.webhooks;

import fi.iki.elonen.NanoHTTPD;
import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.handlers.NotificationHandler;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.model.WebhookNotification;
import nl.juraji.biliomi.utility.calculate.NumberConverter;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.CoreSetting;
import nl.juraji.biliomi.utility.events.EventBus;
import nl.juraji.biliomi.utility.types.Restartable;
import nl.juraji.biliomi.utility.types.collections.TimedList;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
  private TimedList<String> notificationIdHistory;

  @Inject
  @CoreSetting("biliomi.twitch.webhookCallbackUrl")
  private String webhookCallbackUrl;

  @Inject
  private Logger logger;

  @Inject
  private EventBus eventBus;

  @Override
  public void start() {
    int serverPort = 0;

    try {
      URL url = new URL(webhookCallbackUrl);
      serverPort = url.getPort();
    } catch (MalformedURLException e) {
      logger.error("Failed reading configured webhook callback url", e);
      // If this fails shut down Biliomi
      BiliomiContainer.getContainer().shutdownNow(1);
    }

    try {
      this.httpServer = new WebhookReceiverServer(serverPort, this::handleSessionInput);
      this.notificationIdHistory = new TimedList<>("WebhookReceiverNotificationIdHistory");
      this.httpServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
      logger.info("Started HTTP server for Twitch webhook updates");
    } catch (IOException e) {
      logger.error("Failed starting HTTP server for Twitch webhook updates", e);
    }
  }

  @Override
  public void stop() {
    if (this.httpServer != null) {
      this.httpServer.stop();
      this.httpServer = null;
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
            this.notificationIdHistory.add(notification.getId(), 1, TimeUnit.HOURS);
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
