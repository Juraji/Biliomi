package nl.juraji.biliomi.model.core;

import nl.juraji.biliomi.utility.types.hibernatetypes.DateTimeISO8601Type;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 10-4-2017.
 * biliomi
 */
@Entity
@XmlRootElement(name = "User")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement(name = "Id")
    private long id;

    @Column(unique = true)
    @NotNull
    @XmlElement(name = "Username")
    private String username;

    @Column
    @NotNull
    @XmlElement(name = "DisplayName")
    private String displayName;

    @Column(unique = true)
    @NotNull
    @XmlElement(name = "TwitchUserId")
    private long twitchUserId;

    @NotNull
    @ManyToOne
    @XmlElement(name = "UserGroup")
    private UserGroup userGroup;

    @Column
    @XmlElement(name = "Title")
    private String title;

    @Column
    @ColumnDefault("0")
    @XmlElement(name = "RecordedTime")
    private long recordedTime;

    @Column
    @ColumnDefault("0")
    @XmlElement(name = "Points")
    private long points;

    @Column
    @ColumnDefault("FALSE")
    @XmlElement(name = "Caster")
    private boolean caster;

    @Column
    @ColumnDefault("FALSE")
    @XmlElement(name = "Moderator")
    private boolean moderator;

    @Column
    @ColumnDefault("FALSE")
    @XmlElement(name = "Follower")
    private boolean follower;

    @Column
    @Type(type = DateTimeISO8601Type.TYPE)
    @XmlElement(name = "FollowDate")
    private DateTime followDate;

    @Column
    @XmlElement(name = "Subscriber")
    private boolean subscriber;

    @Column
    @Type(type = DateTimeISO8601Type.TYPE)
    @XmlElement(name = "SubscribeDate")
    private DateTime subscribeDate;

    @Column
    @Type(type = DateTimeISO8601Type.TYPE)
    @XmlElement(name = "BlacklistedSince")
    private DateTime blacklistedSince;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getTwitchUserId() {
        return twitchUserId;
    }

    public void setTwitchUserId(long twitchUserId) {
        this.twitchUserId = twitchUserId;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getRecordedTime() {
        return recordedTime;
    }

    public void setRecordedTime(long recordedTime) {
        this.recordedTime = recordedTime;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public void addPoints(long amount) {
        this.points += amount;
    }

    public void takePoints(long amount) {
        this.points -= amount;
    }

    public boolean isCaster() {
        return caster;
    }

    public void setCaster(boolean caster) {
        this.caster = caster;
    }

    public boolean isModerator() {
        return moderator;
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
    }

    public boolean isFollower() {
        return follower;
    }

    public void setFollower(boolean follower) {
        this.follower = follower;
    }

    public DateTime getFollowDate() {
        return followDate;
    }

    public void setFollowDate(DateTime followDate) {
        this.followDate = followDate;
    }

    public boolean isSubscriber() {
        return subscriber;
    }

    public void setSubscriber(boolean subscriber) {
        this.subscriber = subscriber;
    }

    public DateTime getSubscribeDate() {
        return subscribeDate;
    }

    public void setSubscribeDate(DateTime subscribeDate) {
        this.subscribeDate = subscribeDate;
    }

    public DateTime getBlacklistedSince() {
        return blacklistedSince;
    }

    public void setBlacklistedSince(DateTime blacklistedSince) {
        this.blacklistedSince = blacklistedSince;
    }

    public String getNameAndTitle() {
        if (title == null) {
            return displayName;
        } else {
            return title + ' ' + displayName;
        }
    }
}