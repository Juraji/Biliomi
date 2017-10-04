package nl.juraji.biliomi.model.internal.events.irc.channel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 23-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "IrcChannelJoinedEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class IrcChannelJoinedEvent extends IrcChannelEvent {

  @XmlElement(name = "Username")
  private final String username;

  public IrcChannelJoinedEvent(String channelName, String username) {
    super(channelName);
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
