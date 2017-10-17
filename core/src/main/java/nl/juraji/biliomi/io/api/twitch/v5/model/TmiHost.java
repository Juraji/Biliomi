package nl.juraji.biliomi.io.api.twitch.v5.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TmiHost")
@XmlAccessorType(XmlAccessType.FIELD)
public class TmiHost {

  @XmlElement(name = "host_id")
  private Integer hostId;

  @XmlElement(name = "target_id")
  private Integer targetId;

  @XmlElement(name = "host_login")
  private String hostUsername;

  @XmlElement(name = "target_login")
  private String targetUsername;

  @XmlElement(name = "host_display_name")
  private String hostDisplayName;

  @XmlElement(name = "target_display_name")
  private String targetDisplayName;

  public Integer getHostId() {
    return hostId;
  }

  public void setHostId(Integer hostId) {
    this.hostId = hostId;
  }

  public Integer getTargetId() {
    return targetId;
  }

  public void setTargetId(Integer targetId) {
    this.targetId = targetId;
  }

  public String getHostUsername() {
    return hostUsername;
  }

  public void setHostUsername(String hostUsername) {
    this.hostUsername = hostUsername;
  }

  public String getTargetUsername() {
    return targetUsername;
  }

  public void setTargetUsername(String targetUsername) {
    this.targetUsername = targetUsername;
  }

  public String getHostDisplayName() {
    return hostDisplayName;
  }

  public void setHostDisplayName(String hostDisplayName) {
    this.hostDisplayName = hostDisplayName;
  }

  public String getTargetDisplayName() {
    return targetDisplayName;
  }

  public void setTargetDisplayName(String targetDisplayName) {
    this.targetDisplayName = targetDisplayName;
  }
}
