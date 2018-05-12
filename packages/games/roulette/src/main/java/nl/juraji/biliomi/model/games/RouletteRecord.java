package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.types.hibernatetypes.DateTimeISO8601Type;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 24-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "RouletteRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class RouletteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement(name = "Id")
    private long id;

    @OneToOne
    @NotNull
    @XmlElement(name = "User")
    private User user;

    @Column
    @XmlElement(name = "Fatal")
    private boolean fatal;

    @Column
    @NotNull
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

    public boolean isFatal() {
        return fatal;
    }

    public void setFatal(boolean fatal) {
        this.fatal = fatal;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }
}
