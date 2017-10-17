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
 * Created by Juraji on 22-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "KillRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class KillRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @XmlElement(name = "Id")
  private long id;

  @OneToOne
  @NotNull
  @XmlElement(name = "Killer")
  private User killer;

  @OneToOne
  @NotNull
  @XmlElement(name = "Target")
  private User target;

  @Column
  @XmlElement(name = "IsSuicide")
  private boolean isSuicide;

  @Column
  @NotNull
  @Type(type = DateTimeISO8601Type.TYPE)
  @XmlElement(name = "Date")
  private DateTime date;

  public long getId() {
    return id;
  }

  public User getKiller() {
    return killer;
  }

  public void setKiller(User killer) {
    this.killer = killer;
  }

  public User getTarget() {
    return target;
  }

  public void setTarget(User target) {
    this.target = target;
  }

  public boolean isSuicide() {
    return isSuicide;
  }

  public void setSuicide(boolean suicide) {
    isSuicide = suicide;
  }

  public DateTime getDate() {
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }
}
