package nl.juraji.biliomi.io.api.spotify.v1.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 1-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "SpotifySnapshot")
@XmlAccessorType(XmlAccessType.FIELD)
public class SpotifySnapshot {
  @XmlElement(name = "snapshot_id")
  private String snapshotId;

  public String getSnapshotId() {
    return snapshotId;
  }

  public void setSnapshotId(String snapshotId) {
    this.snapshotId = snapshotId;
  }
}
