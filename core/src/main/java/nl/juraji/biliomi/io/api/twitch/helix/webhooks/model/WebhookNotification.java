package nl.juraji.biliomi.io.api.twitch.helix.webhooks.model;

import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
@XmlRootElement(name = "WebhookNotification")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class WebhookNotification<T> {

  @XmlElement(name = "id")
  private String id;

  @XmlElement(name = "topic")
  private String topic;

  @XmlElement(name = "type")
  private String type;

  @XmlElement(name = "timestamp")
  private DateTime timestamp;

  @XmlElement(name = "data")
  private T data;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public DateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(DateTime timestamp) {
    this.timestamp = timestamp;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
}
