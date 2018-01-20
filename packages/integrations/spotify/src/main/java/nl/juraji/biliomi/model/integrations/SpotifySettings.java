package nl.juraji.biliomi.model.integrations;

import nl.juraji.biliomi.model.core.settings.Settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
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

  @Column
  @XmlElement(name = "MaxDuration")
  private long maxDuration;

  @Transient
  @XmlElement(name = "_IntegrationEnabled")
  private boolean _integrationEnabled = false;

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

  public boolean is_integrationEnabled() {
    return _integrationEnabled;
  }

  public void set_integrationEnabled(boolean _integrationEnabled) {
    this._integrationEnabled = _integrationEnabled;
  }

  @Override
  public void setDefaultValues() {
    songrequestsEnabled = false;
    maxDuration = 480000;
  }
}
