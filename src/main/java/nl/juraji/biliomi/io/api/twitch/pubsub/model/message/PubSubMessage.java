package nl.juraji.biliomi.io.api.twitch.pubsub.model.message;

import nl.juraji.biliomi.io.api.twitch.pubsub.model.types.PubSubErrorType;
import nl.juraji.biliomi.io.api.twitch.pubsub.model.types.PubSubMessageType;

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
public class PubSubMessage {

  @XmlElement(name = "type")
  private PubSubMessageType type;

  @XmlElement(name = "data")
  private String data;

  @XmlElement(name = "nonce")
  private String nonce;

  @XmlElement(name = "error")
  private PubSubErrorType error;

  public PubSubMessageType getType() {
    return type;
  }

  public void setType(PubSubMessageType type) {
    this.type = type;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getNonce() {
    return nonce;
  }

  public void setNonce(String nonce) {
    this.nonce = nonce;
  }

  public PubSubErrorType getError() {
    return error;
  }

  public void setError(PubSubErrorType error) {
    this.error = error;
  }
}
