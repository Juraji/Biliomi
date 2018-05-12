package nl.juraji.biliomi.model.core;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 15-12-2017.
 * Biliomi
 */
@Entity
@XmlRootElement(name = "Community")
@XmlAccessorType(XmlAccessType.FIELD)
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement(name = "Id")
    private long id;

    @Column
    @XmlElement(name = "TwitchId")
    private String twitchId;

    @Column(unique = true)
    @XmlElement(name = "Name")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @XmlElement(name = "Owner")
    private User owner;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTwitchId() {
        return twitchId;
    }

    public void setTwitchId(String twitchId) {
        this.twitchId = twitchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
