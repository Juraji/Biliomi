package nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.socket.model.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "HostMessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class HostMessage {

  @XmlElement(name = "name")
  private String name;

  @XmlElement(name = "viewers")
  private int viewers;

  @XmlElement(name = "type")
  private HostType type;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getViewers() {
    return viewers;
  }

  public void setViewers(int viewers) {
    this.viewers = viewers;
  }

  public HostType getType() {
    return type;
  }

  public void setType(HostType type) {
    this.type = type;
  }

  public enum HostType {
    MANUAL, AUTO
  }
}
