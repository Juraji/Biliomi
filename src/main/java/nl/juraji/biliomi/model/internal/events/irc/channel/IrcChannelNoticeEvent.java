package nl.juraji.biliomi.model.internal.events.irc.channel;

import nl.juraji.biliomi.io.api.twitch.irc.utils.Tags;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 25-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "IrcChannelNoticeEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class IrcChannelNoticeEvent extends IrcChannelEvent {

  @XmlElement(name = "Tags")
  private final Tags tags;

  @XmlElement(name = "Message")
  private final String message;

  public IrcChannelNoticeEvent(String channelName, Tags tags, String message) {
    super(channelName);
    this.tags = tags;
    this.message = message;
  }

  public Tags getTags() {
    return tags;
  }

  public Tags.MsgId getMsgId() {
    return tags.getMsgId();
  }

  public String getMessage() {
    return message;
  }
}
