package nl.juraji.biliomi.io.api.twitch.pubsub.model.commands;

import nl.juraji.biliomi.io.api.twitch.pubsub.model.types.PubSubTopic;
import nl.juraji.biliomi.utility.factories.ModelUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * Created by Juraji on 3-9-2017.
 * Biliomi v3
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PubSubListenCommandData {

  @XmlElement(name = "topics")
  private Set<String> topics;

  @XmlElement(name = "auth_token")
  private String authToken;

  public Set<String> getTopics() {
    this.topics = ModelUtils.initCollection(this.topics);
    return topics;
  }

  public void addTopic(PubSubTopic topic, long channelId) {
    getTopics().add(topic.getTopic() + '.' + channelId);
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }
}
