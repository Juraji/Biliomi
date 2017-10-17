package nl.juraji.biliomi.model.internal.events.twitch.bits;

import nl.juraji.biliomi.model.internal.events.twitch.TwitchEvent;

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
public class TwitchBitsEvent extends TwitchEvent {

  @XmlElement(name = "Username")
  private String username;

  @XmlElement(name = "UserId")
  private long userId;

  @XmlElement(name = "BitsUsed")
  private long bitsUsed;

  @XmlElement(name = "TotalBitsUsed")
  private long totalBitsUsed;

  public TwitchBitsEvent(String username, long userId, long bitsUsed, long totalBitsUsed) {
    this.username = username;
    this.userId = userId;
    this.bitsUsed = bitsUsed;
    this.totalBitsUsed = totalBitsUsed;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public long getBitsUsed() {
    return bitsUsed;
  }

  public void setBitsUsed(long bitsUsed) {
    this.bitsUsed = bitsUsed;
  }

  public long getTotalBitsUsed() {
    return totalBitsUsed;
  }

  public void setTotalBitsUsed(long totalBitsUsed) {
    this.totalBitsUsed = totalBitsUsed;
  }
}
