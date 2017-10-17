package nl.juraji.biliomi.io.api.spotify.v1.model.tracks;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@XmlRootElement(name = "SpotifyArtist")
@XmlAccessorType(XmlAccessType.FIELD)
public class SpotifyArtist {

  @XmlElement(name = "id")
  private String id;

  @XmlElement(name = "name")
  private String name;

  @XmlElement(name = "uri")
  private String uri;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }
}
