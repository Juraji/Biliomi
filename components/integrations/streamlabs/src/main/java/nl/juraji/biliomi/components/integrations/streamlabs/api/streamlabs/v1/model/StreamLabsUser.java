package nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.v1.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "StreamLabsUser")
@XmlAccessorType(XmlAccessType.FIELD)
public class StreamLabsUser {

  @XmlElement(name = "id")
  private String id;

  @XmlElement(name = "display_name")
  private String displayName;

  @XmlElement(name = "name")
  private String username;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
