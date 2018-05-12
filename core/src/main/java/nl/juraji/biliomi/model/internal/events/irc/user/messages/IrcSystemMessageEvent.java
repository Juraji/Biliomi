package nl.juraji.biliomi.model.internal.events.irc.user.messages;

import nl.juraji.biliomi.io.api.twitch.irc.utils.Tags;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 23-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "IrcSystemMessageEvent")
public class IrcSystemMessageEvent extends IrcMessageEvent {
    public IrcSystemMessageEvent(String username, Tags tags, String message) {
        super(username, tags, message);
    }
}
