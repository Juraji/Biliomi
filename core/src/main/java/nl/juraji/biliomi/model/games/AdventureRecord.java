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
 * Created by Juraji on 5-6-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "AdventureRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class AdventureRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @XmlElement(name = "Id")
  private long id;

  @OneToOne
  @NotNull
  @XmlElement(name = "Adventurer")
  private User adventurer;

  @Column
  @XmlElement(name = "Bet")
  private long bet;

  @Column
  @XmlElement(name = "Payout")
  private long payout;

  @Column
  @XmlElement(name = "ByTamagotchi")
  private boolean byTamagotchi;

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

  public User getAdventurer() {
    return adventurer;
  }

  public void setAdventurer(User adventurer) {
    this.adventurer = adventurer;
  }

  public long getBet() {
    return bet;
  }

  public void setBet(long bet) {
    this.bet = bet;
  }

  public long getPayout() {
    return payout;
  }

  public void setPayout(long payout) {
    this.payout = payout;
  }

  public boolean isByTamagotchi() {
    return byTamagotchi;
  }

  public void setByTamagotchi(boolean byTamagotchi) {
    this.byTamagotchi = byTamagotchi;
  }

  public DateTime getDate() {
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }
}
