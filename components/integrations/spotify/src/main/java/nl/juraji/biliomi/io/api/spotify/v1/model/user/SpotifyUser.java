package nl.juraji.biliomi.io.api.spotify.v1.model.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@XmlRootElement(name = "SpotifyUser")
@XmlAccessorType(XmlAccessType.FIELD)
public class SpotifyUser {

  @XmlElement(name = "id")
  private String id;

  @XmlElement(name = "display_name")
  private String displayName;

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
}
