package nl.juraji.biliomi.model.registers;

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
@XmlRootElement(name = "Donation")
@XmlAccessorType(XmlAccessType.FIELD)
public class Donation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @XmlElement(name = "Id")
  private long id;

  @Column
  @NotNull
  @XmlElement(name = "Donation")
  private String donation;

  @Column
  @XmlElement(name = "Note")
  private String note;

  @OneToOne
  @NotNull
  @XmlElement(name = "User")
  private User user;

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

  public String getDonation() {
    return donation;
  }

  public void setDonation(String donation) {
    this.donation = donation;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
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
}
