package nl.juraji.biliomi.io.api.twitch.helix.webhooks.handlers;

import nl.juraji.biliomi.io.api.twitch.helix.webhooks.model.NewFollowWebhookNotification;
import nl.juraji.biliomi.model.internal.events.twitch.followers.TwitchFollowEvent;
import nl.juraji.biliomi.utility.calculate.NumberConverter;
import nl.juraji.biliomi.utility.events.EventBus;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;

import java.io.IOException;

/**
 * Created by Juraji on 3-1-2018.
 * Biliomi
 */
public class FollowsNotificationHandler implements NotificationHandler<NewFollowWebhookNotification> {

  @Override
  public NewFollowWebhookNotification unmarshalNotification(String notificationData) throws IOException {
    return JacksonMarshaller.unmarshal(notificationData, NewFollowWebhookNotification.class);
  }

  @Override
  public void handleNotification(EventBus eventBus, NewFollowWebhookNotification notification) {
    long userId = NumberConverter.asNumber(notification.getData().getFromId()).toLong();
    eventBus.post(new TwitchFollowEvent(null, userId, notification.getTimestamp()));
  }
}
