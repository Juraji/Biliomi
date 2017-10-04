package nl.juraji.biliomi.model.core.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 22-4-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "TimeTrackingSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeTrackingSettings extends Settings {

  @Column
  @XmlElement(name = "TrackOnline")
  private boolean trackOnline;

  @Column
  @XmlElement(name = "TrackOffline")
  private boolean trackOffline;

  public boolean isTrackOnline() {
    return trackOnline;
  }

  public void setTrackOnline(boolean trackOnlineTime) {
    this.trackOnline = trackOnlineTime;
  }

  public boolean isTrackOffline() {
    return trackOffline;
  }

  public void setTrackOffline(boolean trackOfflineTime) {
    this.trackOffline = trackOfflineTime;
  }

  @Override
  public void setDefaultValues() {
    trackOnline = true;
    trackOffline = false;
  }
}
