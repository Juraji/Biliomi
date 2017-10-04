package nl.juraji.biliomi.model.internal.events.irc.channel;

import nl.juraji.biliomi.model.internal.events.irc.IrcEvent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 23-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "IrcChannelEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class IrcChannelEvent extends IrcEvent {

  @XmlElement(name = "ChannelName")
  private final String channelName;

  public IrcChannelEvent(String channelName) {
    this.channelName = channelName;
  }

  public String getChannelName() {
    return channelName;
  }
}
