package nl.juraji.biliomi.io.api.twitch.helix.webhooks.handlers;

import nl.juraji.biliomi.io.api.twitch.helix.model.TwitchStream;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.model.StreamsWebhookNotification;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.model.internal.events.twitch.webhook.ChannelStateEvent;
import nl.juraji.biliomi.utility.events.EventBus;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Juraji on 3-1-2018.
 * Biliomi
 */
public class StreamsNotificationHandler implements NotificationHandler<StreamsWebhookNotification> {

  @Override
  public StreamsWebhookNotification unmarshalNotification(String notificationData) throws IOException {
    return JacksonMarshaller.unmarshal(notificationData, StreamsWebhookNotification.class);
  }

  @Override
  public void handleNotification(EventBus eventBus, StreamsWebhookNotification notification) {
    if (notification.getData() == null || notification.getData().isEmpty()) {
      Map<String, String> topicQuery = Url.unpackQueryString(notification.getTopic());
      eventBus.post(new ChannelStateEvent(topicQuery.get("user_id"), false, null));
    } else {
      TwitchStream twitchStream = notification.getData().get(0);
      eventBus.post(new ChannelStateEvent(twitchStream.getUserId(), true, twitchStream.getGameId()));
    }
  }
}
