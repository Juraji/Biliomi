package nl.juraji.biliomi.io.api.patreon.v1.model.users;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 12-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonUserAttributes")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonUserAttributes {

  @XmlElement(name = "first_name")
  private String firstName;

  @XmlElement(name = "vanity")
  private String vanity;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getVanity() {
    return vanity;
  }

  public void setVanity(String vanity) {
    this.vanity = vanity;
  }
}
