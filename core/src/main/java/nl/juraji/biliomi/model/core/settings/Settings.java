package nl.juraji.biliomi.model.core.settings;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 10-4-2017.
 * biliomi
 */
@Entity
@XmlRootElement(name = "Settings")
@Table(name = "Settings")
@XmlAccessorType(XmlAccessType.FIELD)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Settings {

  public abstract void setDefaultValues();

  @Id
  @XmlElement(name = "Type")
  private String id = getClass().getSimpleName();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}