package nl.juraji.biliomi.io.api.twitch.v5.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchFollower")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchFollower {

    @XmlElement(name = "created_at")
    private String createdAt;

    @XmlElement(name = "notifications")
    private boolean notifications;

    @XmlElement(name = "user")
    private TwitchUser user;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public TwitchUser getUser() {
        return user;
    }

    public void setUser(TwitchUser user) {
        this.user = user;
    }
}
