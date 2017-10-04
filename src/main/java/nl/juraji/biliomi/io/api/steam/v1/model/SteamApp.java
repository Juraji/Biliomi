package nl.juraji.biliomi.io.api.steam.v1.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 25-5-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "SteamApp")
@XmlAccessorType(XmlAccessType.FIELD)
public class SteamApp {

  @XmlElement(name = "appid")
  private long appId;

  @XmlElement(name = "name")
  private String name;

  @XmlElement(name = "playtime_forever")
  private long playtimeForever;

  @XmlElement(name = "img_icon_url")
  private String imgIconUrl;

  @XmlElement(name = "img_logo_url")
  private String imgLogoUrl;

  @XmlElement(name = "has_community_visible_stats")
  private boolean hasCommunityVisibleStats;

  public long getAppId() {
    return appId;
  }

  public void setAppId(long appId) {
    this.appId = appId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getPlaytimeForever() {
    return playtimeForever;
  }

  public void setPlaytimeForever(long playtimeForever) {
    this.playtimeForever = playtimeForever;
  }

  public String getImgIconUrl() {
    return imgIconUrl;
  }

  public void setImgIconUrl(String imgIconUrl) {
    this.imgIconUrl = imgIconUrl;
  }

  public String getImgLogoUrl() {
    return imgLogoUrl;
  }

  public void setImgLogoUrl(String imgLogoUrl) {
    this.imgLogoUrl = imgLogoUrl;
  }

  public boolean isHasCommunityVisibleStats() {
    return hasCommunityVisibleStats;
  }

  public void setHasCommunityVisibleStats(boolean hasCommunityVisibleStats) {
    this.hasCommunityVisibleStats = hasCommunityVisibleStats;
  }
}
