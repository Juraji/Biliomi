package nl.juraji.biliomi.io.api.spotify.v1.model.player;

import nl.juraji.biliomi.io.api.spotify.v1.model.tracks.SpotifyTrack;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@XmlRootElement(name = "SpotifyCurrentTrack")
@XmlAccessorType(XmlAccessType.FIELD)
public class SpotifyCurrentTrack extends SpotifyTrack {

  @XmlElement(name = "timestamp")
  private long timestamp;

  @XmlElement(name = "progress_ms")
  private long progressMs;

  @XmlElement(name = "is_playing")
  private boolean isPlaying;

  @XmlElement(name = "item")
  private SpotifyTrack item;

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public long getProgressMs() {
    return progressMs;
  }

  public void setProgressMs(long progressMs) {
    this.progressMs = progressMs;
  }

  public boolean isPlaying() {
    return isPlaying;
  }

  public void setPlaying(boolean playing) {
    isPlaying = playing;
  }

  public SpotifyTrack getItem() {
    return item;
  }

  public void setItem(SpotifyTrack item) {
    this.item = item;
  }
}
