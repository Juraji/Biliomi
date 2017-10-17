package nl.juraji.biliomi.io.api.twitch.v5.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 8-9-2017.
 * Biliomi v3
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchTeam {

  @XmlElement(name = "_id")
  private Long id;

  @XmlElement(name = "background")
  private String background;

  @XmlElement(name = "banner")
  private String banner;

  @XmlElement(name = "created_at")
  private String createdAt;

  @XmlElement(name = "display_name")
  private String displayName;

  @XmlElement(name = "info")
  private String info;

  @XmlElement(name = "logo")
  private String logo;

  @XmlElement(name = "name")
  private String name;

  @XmlElement(name = "updated_at")
  private String updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBackground() {
    return background;
  }

  public void setBackground(String background) {
    this.background = background;
  }

  public String getBanner() {
    return banner;
  }

  public void setBanner(String banner) {
    this.banner = banner;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public String getLogo() {
    return logo;
  }

  public void setLogo(String logo) {
    this.logo = logo;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }
}
