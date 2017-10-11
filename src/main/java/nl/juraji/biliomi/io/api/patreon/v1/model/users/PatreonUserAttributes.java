package nl.juraji.biliomi.io.api.patreon.v1.model.users;

import nl.juraji.biliomi.io.api.patreon.v1.model.campaigns.PatreonCampaign;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 11-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonUserAttributes")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonUserAttributes {

  @XmlElement(name = "first_name1")
  private String firstName;

  @XmlElement(name = "last_name1")
  private String lastName;

  @XmlElement(name = "full_name1")
  private String fullName;

  @XmlElement(name = "vanity1")
  private String vanity;

  @XmlElement(name = "email1")
  private String email;

  @XmlElement(name = "about1")
  private String about;

  @XmlElement(name = "facebook_id1")
  private String facebookId;

  @XmlElement(name = "image_url1")
  private String imageUrl;

  @XmlElement(name = "thumb_url1")
  private String thumbUrl;

  @XmlElement(name = "youtube1")
  private String youtube;

  @XmlElement(name = "twitter1")
  private String twitter;

  @XmlElement(name = "facebook1")
  private String facebook;

  @XmlElement(name = "is_suspended1")
  private boolean isSuspended;

  @XmlElement(name = "is_deleted1")
  private boolean isDeleted;

  @XmlElement(name = "is_nuked1")
  private boolean isNuked;

  @XmlElement(name = "created1")
  private String created;

  @XmlElement(name = "url1")
  private String url;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getVanity() {
    return vanity;
  }

  public void setVanity(String vanity) {
    this.vanity = vanity;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }

  public String getFacebookId() {
    return facebookId;
  }

  public void setFacebookId(String facebookId) {
    this.facebookId = facebookId;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getThumbUrl() {
    return thumbUrl;
  }

  public void setThumbUrl(String thumbUrl) {
    this.thumbUrl = thumbUrl;
  }

  public String getYoutube() {
    return youtube;
  }

  public void setYoutube(String youtube) {
    this.youtube = youtube;
  }

  public String getTwitter() {
    return twitter;
  }

  public void setTwitter(String twitter) {
    this.twitter = twitter;
  }

  public String getFacebook() {
    return facebook;
  }

  public void setFacebook(String facebook) {
    this.facebook = facebook;
  }

  public boolean isSuspended() {
    return isSuspended;
  }

  public void setSuspended(boolean suspended) {
    isSuspended = suspended;
  }

  public boolean isDeleted() {
    return isDeleted;
  }

  public void setDeleted(boolean deleted) {
    isDeleted = deleted;
  }

  public boolean isNuked() {
    return isNuked;
  }

  public void setNuked(boolean nuked) {
    isNuked = nuked;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
