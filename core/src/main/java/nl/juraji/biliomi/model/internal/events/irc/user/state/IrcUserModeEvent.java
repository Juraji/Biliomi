package nl.juraji.biliomi.model.internal.events.irc.user.state;

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
@XmlRootElement(name = "IrcUserModeEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class IrcUserModeEvent extends IrcUserEvent {

    @XmlElement(name = "IsModeratorMode")
    private final boolean isModeratorMode;

    public IrcUserModeEvent(String username, Tags tags, boolean isModeratorMode) {
        super(username, tags);
        this.isModeratorMode = isModeratorMode;
    }

    public boolean isModeratorMode() {
        return isModeratorMode;
    }
}
