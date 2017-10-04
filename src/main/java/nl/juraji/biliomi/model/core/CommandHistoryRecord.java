package nl.juraji.biliomi.model.core;

import nl.juraji.biliomi.utility.types.hibernatetypes.DateTimeISO8601Type;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 22-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "CommandHistoryRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommandHistoryRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @XmlElement(name = "Id")
  private long id;

  @Column
  @XmlElement(name = "Command")
  private String command;

  @Column
  @XmlElement(name = "TriggeredBy")
  private String triggeredBy;

  @Column
  @XmlElement(name = "Arguments")
  private String arguments;

  @Column
  @Type(type = DateTimeISO8601Type.TYPE)
  @XmlElement(name = "Date")
  private DateTime date;

  @OneToOne
  @XmlElement(name = "User")
  private User user;

  @Column
  @XmlElement(name = "Success")
  private boolean success;

  public long getId() {
    return id;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getArguments() {
    return arguments;
  }

  public void setArguments(String arguments) {
    this.arguments = arguments;
  }

  public DateTime getDate() {
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public void setTriggeredBy(String usedAlias) {
    this.triggeredBy = usedAlias;
  }

  public String getTriggeredBy() {
    return triggeredBy;
  }
}
