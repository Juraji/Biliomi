package nl.juraji.biliomi.model.registers;

import nl.juraji.biliomi.model.core.Game;
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
 * Created by Juraji on 23-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "Quote")
@XmlAccessorType(XmlAccessType.FIELD)
public class Quote {

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

  @Column
  @NotNull
  @Type(type = DateTimeISO8601Type.TYPE)
  @XmlElement(name = "Date")
  private DateTime date;

  @OneToOne
  @XmlElement(name = "GameAtMoment")
  private Game gameAtMoment;

  public long getId() {
    return id;
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

  public DateTime getDate() {
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }

  public Game getGameAtMoment() {
    return gameAtMoment;
  }

  public void setGameAtMoment(Game gameAtMoment) {
    this.gameAtMoment = gameAtMoment;
  }
}
