package nl.juraji.biliomi.model.internal.events.twitch.followers;

import nl.juraji.biliomi.model.core.User;
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

    @XmlElement(name = "User")
    private final User user;

    @XmlElement(name = "FollowsSince")
    private final DateTime followsSince;

    public TwitchFollowEvent(User user, DateTime followsSince) {
        this.user = user;
        this.followsSince = followsSince;
    }

    public User getUser() {
        return user;
    }

    public DateTime getFollowsSince() {
        return followsSince;
    }
}
