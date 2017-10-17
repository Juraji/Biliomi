package nl.juraji.biliomi.model.core;

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
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "Game")
@XmlAccessorType(XmlAccessType.FIELD)
public class Game {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @XmlElement(name = "Id")
  private long id;

  @Column(unique = true)
  @NotNull
  @XmlElement(name = "Name")
  private String name;

  @Column
  @NotNull
  @Type(type = DateTimeISO8601Type.TYPE)
  @XmlElement(name = "FirstPlayedOn")
  private DateTime firstPlayedOn;

  @Column
  @XmlElement(name = "SteamId")
  private Long steamId;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DateTime getFirstPlayedOn() {
    return firstPlayedOn;
  }

  public void setFirstPlayedOn(DateTime firstPlayedOn) {
    this.firstPlayedOn = firstPlayedOn;
  }

  public Long getSteamId() {
    return steamId;
  }

  public void setSteamId(Long steamId) {
    this.steamId = steamId;
  }
}
