package nl.juraji.biliomi.model.internal.events.twitch.bits;

import nl.juraji.biliomi.model.internal.events.twitch.TwitchEvent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 27-2-2018.
 * Biliomi
 */
@XmlRootElement(name = "IrcUserEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchBitsEvent extends TwitchEvent {

  @XmlElement(name = "Username")
  private final String username;

  @XmlElement(name = "BitsAmount")
  private final long bitsAmount;

  public TwitchBitsEvent(String username, long bitsAmount) {
    this.username = username;
    this.bitsAmount = bitsAmount;
  }

  public String getUsername() {
    return username;
  }

  public long getBitsAmount() {
    return bitsAmount;
  }
}
