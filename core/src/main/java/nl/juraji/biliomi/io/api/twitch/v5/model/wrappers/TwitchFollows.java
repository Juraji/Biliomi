package nl.juraji.biliomi.io.api.twitch.v5.model.wrappers;

import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchFollower;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchFollows")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchFollows extends TwitchList {

    @XmlElement(name = "_cursor")
    private long cursor;

    @XmlElement(name = "follows")
    private List<TwitchFollower> follows;

    public long getCursor() {
        return cursor;
    }

    public void setCursor(long cursor) {
        this.cursor = cursor;
    }

    public List<TwitchFollower> getFollows() {
        return follows;
    }

    public void setFollows(List<TwitchFollower> follows) {
        this.follows = follows;
    }
}
