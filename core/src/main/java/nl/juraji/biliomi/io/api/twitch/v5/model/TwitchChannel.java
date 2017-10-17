package nl.juraji.biliomi.io.api.twitch.v5.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchChannel")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchChannel {

  @XmlElement(name = "_id")
  private int id;

  @XmlElement(name = "broadcaster_language")
  private String broadcasterLanguage;

  @XmlElement(name = "created_at")
  private String createdAt;

  @XmlElement(name = "display_name")
  private String displayName;

  @XmlElement(name = "email")
  private String email;

  @XmlElement(name = "followers")
  private int followers;

  @XmlElement(name = "game")
  private String game;

  @XmlElement(name = "language")
  private String language;

  @XmlElement(name = "logo")
  private String logo;

  @XmlElement(name = "mature")
  private boolean mature;

  @XmlElement(name = "name")
  private String name;

  @XmlElement(name = "partner")
  private boolean partner;

  @XmlElement(name = "profile_banner")
  private String profileBanner;

  @XmlElement(name = "profile_banner_background_color")
  private String profileBannerBackgroundColor;

  @XmlElement(name = "status")
  private String status;

  @XmlElement(name = "stream_key")
  private String streamKey;

  @XmlElement(name = "updated_at")
  private String updatedAt;

  @XmlElement(name = "url")
  private String url;

  @XmlElement(name = "video_banner")
  private String videoBanner;

  @XmlElement(name = "views")
  private int views;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getBroadcasterLanguage() {
    return broadcasterLanguage;
  }

  public void setBroadcasterLanguage(String broadcasterLanguage) {
    this.broadcasterLanguage = broadcasterLanguage;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getFollowers() {
    return followers;
  }

  public void setFollowers(int followers) {
    this.followers = followers;
  }

  public String getGame() {
    return game;
  }

  public void setGame(String game) {
    this.game = game;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getLogo() {
    return logo;
  }

  public void setLogo(String logo) {
    this.logo = logo;
  }

  public boolean isMature() {
    return mature;
  }

  public void setMature(boolean mature) {
    this.mature = mature;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isPartner() {
    return partner;
  }

  public void setPartner(boolean partner) {
    this.partner = partner;
  }

  public String getProfileBanner() {
    return profileBanner;
  }

  public void setProfileBanner(String profileBanner) {
    this.profileBanner = profileBanner;
  }

  public String getProfileBannerBackgroundColor() {
    return profileBannerBackgroundColor;
  }

  public void setProfileBannerBackgroundColor(String profileBannerBackgroundColor) {
    this.profileBannerBackgroundColor = profileBannerBackgroundColor;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getStreamKey() {
    return streamKey;
  }

  public void setStreamKey(String streamKey) {
    this.streamKey = streamKey;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getVideoBanner() {
    return videoBanner;
  }

  public void setVideoBanner(String videoBanner) {
    this.videoBanner = videoBanner;
  }

  public int getViews() {
    return views;
  }

  public void setViews(int views) {
    this.views = views;
  }
}
