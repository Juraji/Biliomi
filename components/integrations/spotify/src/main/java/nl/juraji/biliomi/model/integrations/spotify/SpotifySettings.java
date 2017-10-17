package nl.juraji.biliomi.model.integrations.spotify;

import nl.juraji.biliomi.model.core.settings.Settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 1-10-2017.
 * Biliomi
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SpotifySettings extends Settings {

  @Column
  @XmlElement(name = "SongrequestsEnabled")
  private boolean songrequestsEnabled;

  @Column
  @XmlElement(name = "SongRequestPlaylistId")
  private String songRequestPlaylistId;
  private long maxDuration;

  public boolean isSongrequestsEnabled() {
    return songrequestsEnabled;
  }

  public void setSongrequestsEnabled(boolean songrequestsEnabled) {
    this.songrequestsEnabled = songrequestsEnabled;
  }

  public String getSongRequestPlaylistId() {
    return songRequestPlaylistId;
  }

  public void setSongRequestPlaylistId(String songRequestPlaylistId) {
    this.songRequestPlaylistId = songRequestPlaylistId;
  }

  public long getMaxDuration() {
    return maxDuration;
  }

  public void setMaxDuration(long maxDuration) {
    this.maxDuration = maxDuration;
  }

  @Override
  public void setDefaultValues() {
    songrequestsEnabled = false;
    maxDuration = 480000;
  }
}
