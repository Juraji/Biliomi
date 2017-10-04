package nl.juraji.biliomi.model.internal.events.twitch.followers;

import nl.juraji.biliomi.model.internal.events.twitch.TwitchEvent;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 27-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchFollowEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchFollowEvent extends TwitchEvent {

  @XmlElement(name = "Username")
  private final String username;

  @XmlElement(name = "TwitchId")
  private final long twitchId;

  @XmlElement(name = "FollowsSince")
  private final DateTime followsSince;

  public TwitchFollowEvent(String username, long twitchId, DateTime followsSince) {
    this.username = username;
    this.twitchId = twitchId;
    this.followsSince = followsSince;
  }

  public String getUsername() {
    return username;
  }

  public DateTime getFollowsSince() {
    return followsSince;
  }

  public long getTwitchId() {
    return twitchId;
  }
}
