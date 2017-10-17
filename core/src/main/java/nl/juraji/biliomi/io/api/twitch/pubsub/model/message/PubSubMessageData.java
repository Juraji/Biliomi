package nl.juraji.biliomi.io.api.twitch.pubsub.model.message;

import nl.juraji.biliomi.io.api.twitch.pubsub.model.types.PubSubTopic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 4-9-2017.
 * Biliomi v3
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PubSubMessageData {

  @XmlElement(name = "topic")
  private PubSubTopic topic;

  @XmlElement(name = "message")
  private String message;

  public PubSubTopic getTopic() {
    return topic;
  }

  public void setTopic(PubSubTopic topic) {
    this.topic = topic;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
