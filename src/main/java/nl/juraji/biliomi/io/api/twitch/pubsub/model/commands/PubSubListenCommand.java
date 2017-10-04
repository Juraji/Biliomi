package nl.juraji.biliomi.io.api.twitch.pubsub.model.commands;

import nl.juraji.biliomi.io.api.twitch.pubsub.model.types.PubSubMessageType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 3-9-2017.
 * Biliomi v3
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PubSubListenCommand implements PubSubCommand {

  @XmlElement(name = "type")
  private PubSubMessageType type = PubSubMessageType.LISTEN;

  @XmlElement(name = "nonce")
  private String nonce;

  @XmlElement(name = "data")
  private PubSubListenCommandData data;

  public PubSubMessageType getType() {
    return type;
  }

  public void setType(PubSubMessageType type) {
    this.type = type;
  }

  public String getNonce() {
    return nonce;
  }

  public void setNonce(String nonce) {
    this.nonce = nonce;
  }

  public PubSubListenCommandData getData() {
    return data;
  }

  public void setData(PubSubListenCommandData data) {
    this.data = data;
  }
}
