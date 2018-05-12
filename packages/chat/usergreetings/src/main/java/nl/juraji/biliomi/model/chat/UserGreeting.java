package nl.juraji.biliomi.model.chat;

import nl.juraji.biliomi.model.core.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 21-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "UserGreeting")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserGreeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement(name = "Id")
    private long id;

    @OneToOne
    @NotNull
    @XmlElement(name = "User")
    private User user;

    @Column
    @NotNull
    @XmlElement(name = "Message")
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
