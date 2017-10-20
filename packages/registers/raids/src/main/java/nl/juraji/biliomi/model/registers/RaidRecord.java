package nl.juraji.biliomi.model.registers;

import nl.juraji.biliomi.model.core.Direction;
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
@XmlRootElement(name = "RaidRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class RaidRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @XmlElement(name = "Id")
  private long id;

  @OneToOne
  @NotNull
  @XmlElement(name = "Channel")
  private User channel;

  @Column
  @NotNull
  @Enumerated(EnumType.STRING)
  @XmlElement(name = "Direction")
  private Direction direction;

  @Column
  @NotNull
  @Type(type = DateTimeISO8601Type.TYPE)
  @XmlElement(name = "Date")
  private DateTime date;

  @Column
  @XmlElement(name = "GameAtMoment")
  private String gameAtMoment;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public User getChannel() {
    return channel;
  }

  public void setChannel(User channel) {
    this.channel = channel;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public DateTime getDate() {
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }

  public String getGameAtMoment() {
    return gameAtMoment;
  }

  public void setGameAtMoment(String wasPlaying) {
    this.gameAtMoment = wasPlaying;
  }
}
