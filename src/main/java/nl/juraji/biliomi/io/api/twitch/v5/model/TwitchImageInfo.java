package nl.juraji.biliomi.io.api.twitch.v5.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchImageInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchImageInfo {

  @XmlElement(name = "small")
  private String small;

  @XmlElement(name = "medium")
  private String medium;

  @XmlElement(name = "large")
  private String large;

  @XmlElement(name = "template")
  private String template;

  public String getSmall() {
    return small;
  }

  public void setSmall(String small) {
    this.small = small;
  }

  public String getMedium() {
    return medium;
  }

  public void setMedium(String medium) {
    this.medium = medium;
  }

  public String getLarge() {
    return large;
  }

  public void setLarge(String large) {
    this.large = large;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }
}
