package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.types.hibernatetypes.DateTimeISO8601Type;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AchievementRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement(name = "Id")
    private long id;

    @ManyToOne(optional = false)
    @XmlElement(name = "User")
    private User user;

    @Column(nullable = false)
    @XmlElement(name = "AchievementId")
    private String achievementId;

    @Column(nullable = false)
    @XmlElement(name = "Achievement")
    private String achievement;

    @Column
    @Type(type = DateTimeISO8601Type.TYPE)
    @XmlElement(name = "Date")
    private DateTime date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(String achievementId) {
        this.achievementId = achievementId;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }
}
