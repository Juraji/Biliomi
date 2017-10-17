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
 * Created by Juraji on 17-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "HostRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class HostRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @XmlElement(name = "Id")
  private long id;

  @NotNull
  @ManyToOne
  @XmlElement(name = "User")
  private User user;

  @Column
  @NotNull
  @Type(type = DateTimeISO8601Type.TYPE)
  @XmlElement(name = "Date")
  private DateTime date;

  @Column
  @XmlElement(name = "AutoHost")
  private boolean autoHost;

  @Column
  @NotNull
  @Enumerated(EnumType.STRING)
  @XmlElement(name = "Direction")
  private Direction direction;

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

  public DateTime getDate() {
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }

  public boolean isAutoHost() {
    return autoHost;
  }

  public void setAutoHost(boolean autoHost) {
    this.autoHost = autoHost;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }
}
