package nl.juraji.biliomi.io.api.twitch.pubsub.util;

import nl.juraji.biliomi.model.internal.events.twitch.TwitchEvent;
import nl.juraji.biliomi.model.internal.events.twitch.bits.TwitchBitsEvent;
import nl.juraji.biliomi.model.internal.events.twitch.subscribers.TwitchSubscriberEvent;
import nl.juraji.biliomi.io.api.twitch.pubsub.model.message.topics.PubSubBitsTopicData;
import nl.juraji.biliomi.io.api.twitch.pubsub.model.message.topics.PubSubBitsTopicDataData;
import nl.juraji.biliomi.io.api.twitch.pubsub.model.message.topics.PubSubSubscriptionTopicData;
import nl.juraji.biliomi.utility.calculate.Numbers;
import org.joda.time.DateTime;

import java.io.IOException;

import static nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller.unmarshal;

/**
 * Created by Juraji on 4-9-2017.
 * Biliomi v3
 */
public class TopicDataToEventMapper {

  public static TwitchEvent bits(String message) throws IOException {
    PubSubBitsTopicData topicData = unmarshal(message, PubSubBitsTopicData.class);
    PubSubBitsTopicDataData data = topicData.getData();
    Long userId = Numbers.asNumber(data.getUserId()).toLong();

    return new TwitchBitsEvent(
        data.getUsername(),
        userId,
        data.getBitsUsed(),
        data.getTotalBitsUsed()
    );
  }

  public static TwitchEvent subscription(String message) throws IOException {
    PubSubSubscriptionTopicData topicData = unmarshal(message, PubSubSubscriptionTopicData.class);
    return new TwitchSubscriberEvent(
        topicData.getUsername(),
        Numbers.asNumber(topicData.getUserId()).toLong(),
        new DateTime(topicData.getTime()),
        topicData.getSubPlan(),
        "resub".equals(topicData.getContext())
    );
  }
}
