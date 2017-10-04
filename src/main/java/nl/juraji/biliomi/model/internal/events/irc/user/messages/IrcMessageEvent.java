package nl.juraji.biliomi.model.internal.events.irc.user.messages;

import nl.juraji.biliomi.io.api.twitch.irc.utils.Tags;
import nl.juraji.biliomi.model.internal.events.irc.user.IrcUserEvent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 23-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "IrcMessageEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class IrcMessageEvent extends IrcUserEvent {

  @XmlElement(name = "Message")
  private final String message;

  public IrcMessageEvent(String username, Tags tags, String message) {
    super(username, tags);
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
