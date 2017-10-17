package nl.juraji.biliomi.io.api.spotify.v1.model.tracks;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 1-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "SpotifyTracksSearchResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class SpotifyTracksSearchResult {

  @XmlElement(name = "tracks")
  private SpotifyTrackPagingObject tracks;

  public SpotifyTrackPagingObject getTracks() {
    return tracks;
  }

  public void setTracks(SpotifyTrackPagingObject tracks) {
    this.tracks = tracks;
  }
}
