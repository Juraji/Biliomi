package nl.juraji.biliomi.io.api.twitch.helix.webhooks.model;

import nl.juraji.biliomi.io.api.twitch.helix.model.TwitchStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 3-1-2018.
 * Biliomi
 */
@XmlRootElement(name = "WebhookNotification")
@XmlAccessorType(XmlAccessType.FIELD)
public class StreamsWebhookNotification extends WebhookNotification<List<TwitchStream>> {
}
