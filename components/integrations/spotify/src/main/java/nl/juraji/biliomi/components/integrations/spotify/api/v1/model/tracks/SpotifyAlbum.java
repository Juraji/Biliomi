package nl.juraji.biliomi.components.integrations.spotify.api.v1.model.tracks;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@XmlRootElement(name = "SpotifyAlbum")
@XmlAccessorType(XmlAccessType.FIELD)
public class SpotifyAlbum {

  @XmlElement(name = "id")
  private String id;

  @XmlElement(name = "album_type")
  private SpotifyAlbumType albumType;

  @XmlElement(name = "artists")
  private Set<SpotifyArtist> artists;

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

  public SpotifyAlbumType getAlbumType() {
    return albumType;
  }

  public void setAlbumType(SpotifyAlbumType albumType) {
    this.albumType = albumType;
  }

  public Set<SpotifyArtist> getArtists() {
    return artists;
  }

  public void setArtists(Set<SpotifyArtist> artists) {
    this.artists = artists;
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
