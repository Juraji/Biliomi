package nl.juraji.biliomi.io.api.patreon.v1.model.users;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 12-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonUser")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonUser {

  @XmlElement(name = "id")
  private String id;

  @XmlElement(name = "attributes")
  private PatreonUserAttributes attributes;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public PatreonUserAttributes getAttributes() {
    return attributes;
  }

  public void setAttributes(PatreonUserAttributes attributes) {
    this.attributes = attributes;
  }
}
