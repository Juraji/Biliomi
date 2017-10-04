package nl.juraji.biliomi.io.api.twitch.pubsub.model.types;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

/**
 * Created by Juraji on 3-9-2017.
 * Biliomi v3
 */
public enum PubSubTopic {
  BITS("channel-bits-events-v1"),
  SUBSCRIPTIONS("channel-subscribe-events-v1"),
  COMMERCE("channel-commerce-events-v1"),
  WHISPERS("whispers");

  private String topic;

  PubSubTopic(String topic) {
    this.topic = topic;
  }

  public String getTopic() {
    return topic;
  }

  @JsonCreator
  public static PubSubTopic fromMessageTopic(String messageTopic) {
    String topic = messageTopic.substring(0, messageTopic.lastIndexOf('.'));
    return Arrays.stream(PubSubTopic.values())
        .filter(pubSubTopic -> pubSubTopic.topic.equalsIgnoreCase(topic))
        .findFirst()
        .orElse(null);
  }
}
