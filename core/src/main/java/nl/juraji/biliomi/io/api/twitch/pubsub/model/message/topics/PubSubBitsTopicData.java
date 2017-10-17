package nl.juraji.biliomi.io.api.twitch.pubsub.model.message.topics;

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
public class PubSubBitsTopicData {

  @XmlElement(name = "data")
  private PubSubBitsTopicDataData data;

  @XmlElement(name = "version")
  private String version;

  @XmlElement(name = "message_type")
  private String messageType;

  @XmlElement(name = "message_id")
  private String messageId;

  public PubSubBitsTopicDataData getData() {
    return data;
  }

  public void setData(PubSubBitsTopicDataData data) {
    this.data = data;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getMessageType() {
    return messageType;
  }

  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }
}
