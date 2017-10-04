package nl.juraji.biliomi.model.internal.events.irc.user.state;

import nl.juraji.biliomi.model.internal.events.irc.user.IrcUserEvent;
import nl.juraji.biliomi.io.api.twitch.irc.utils.Tags;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 23-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "IrcUserJoinedEvent")
public class IrcUserJoinedEvent extends IrcUserEvent {
  public IrcUserJoinedEvent(String username, Tags tags) {
    super(username, tags);
  }
}
