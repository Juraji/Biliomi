package nl.juraji.biliomi.model.internal.events.irc.user;

import nl.juraji.biliomi.io.api.twitch.irc.utils.Tags;
import nl.juraji.biliomi.model.internal.events.irc.IrcEvent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juraji on 23-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "IrcUserEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class IrcUserEvent extends IrcEvent {

  @XmlElement(name = "Username")
  private final String username;

  @XmlElement(name = "Tags")
  private final Tags tags;

  public IrcUserEvent(String username, Tags tags) {
    this.username = username;
    this.tags = tags;
  }

  public String getUsername() {
    return username;
  }

  public Map<String, String> getTags() {
    if (tags == null) {
      return new HashMap<>();
    }
    return tags;
  }
}
