package nl.juraji.biliomi.io.api.twitch.v5.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 8-9-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchFollower")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchCommunity {

  @XmlElement(name = "_id")
  private String id;

  @XmlElement(name = "owner_id")
  private Long ownerId;

  @XmlElement(name = "name")
  private String name;

  @XmlElement(name = "avatar_image_url")
  private String avatarImageUrl;

  @XmlElement(name = "cover_image_url")
  private String coverImageUrl;

  @XmlElement(name = "description")
  private String description;

  @XmlElement(name = "description_html")
  private String descriptionHtml;

  @XmlElement(name = "rules")
  private String rules;

  @XmlElement(name = "rules_html")
  private String rulesHtml;

  @XmlElement(name = "language")
  private String language;

  @XmlElement(name = "summary")
  private String summary;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAvatarImageUrl() {
    return avatarImageUrl;
  }

  public void setAvatarImageUrl(String avatarImageUrl) {
    this.avatarImageUrl = avatarImageUrl;
  }

  public String getCoverImageUrl() {
    return coverImageUrl;
  }

  public void setCoverImageUrl(String coverImageUrl) {
    this.coverImageUrl = coverImageUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescriptionHtml() {
    return descriptionHtml;
  }

  public void setDescriptionHtml(String descriptionHtml) {
    this.descriptionHtml = descriptionHtml;
  }

  public String getRules() {
    return rules;
  }

  public void setRules(String rules) {
    this.rules = rules;
  }

  public String getRulesHtml() {
    return rulesHtml;
  }

  public void setRulesHtml(String rulesHtml) {
    this.rulesHtml = rulesHtml;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }
}
