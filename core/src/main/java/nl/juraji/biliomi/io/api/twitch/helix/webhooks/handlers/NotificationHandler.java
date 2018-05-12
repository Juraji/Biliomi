package nl.juraji.biliomi.io.api.twitch.helix.webhooks.handlers;

import nl.juraji.biliomi.io.api.twitch.helix.webhooks.model.WebhookNotification;
import nl.juraji.biliomi.utility.events.EventBus;

import java.io.IOException;

/**
 * Created by Juraji on 3-1-2018.
 * Biliomi
 */
public interface NotificationHandler<T extends WebhookNotification> {

    T unmarshalNotification(String notificationData) throws IOException;

    void handleNotification(EventBus eventBus, T notification);
}
