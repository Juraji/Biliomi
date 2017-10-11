package nl.juraji.biliomi.io.api.patreon.v1.model.campaigns;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 11-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonCampaignAttributes")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonCampaignAttributes {

  @XmlElement(name = "summary")
  private String summary;

  @XmlElement(name = "creation_name")
  private String creationName;

  @XmlElement(name = "pay_per_name")
  private String payPerName;

  @XmlElement(name = "one_liner")
  private String oneLiner;

  @XmlElement(name = "main_video_embed")
  private String mainBideoEmbed;

  @XmlElement(name = "main_video_url")
  private String mainVideoUrl;

  @XmlElement(name = "image_small_url")
  private String imageSmallUrl;

  @XmlElement(name = "image_url")
  private String imageUrl;

  @XmlElement(name = "thanks_video_url")
  private String thanksVideoUrl;

  @XmlElement(name = "thanks_embed")
  private String thanksEmbed;

  @XmlElement(name = "thanks_msg")
  private String thanksMsg;

  @XmlElement(name = "is_monthly")
  private boolean isMonthly;

  @XmlElement(name = "is_nsfw")
  private boolean isNsfw;

  @XmlElement(name = "created_at")
  private String createdAt;

  @XmlElement(name = "published_at")
  private String publishedAt;

  @XmlElement(name = "pledge_url")
  private String pledgeUrl;

  @XmlElement(name = "pledge_sum")
  private int pledgeSum;

  @XmlElement(name = "patron_count")
  private int patronCount;

  @XmlElement(name = "creation_count")
  private int creationCount;

  @XmlElement(name = "outstanding_payment_amount_cents")
  private int outstandingPaymentAmountCents;

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getCreationName() {
    return creationName;
  }

  public void setCreationName(String creationName) {
    this.creationName = creationName;
  }

  public String getPayPerName() {
    return payPerName;
  }

  public void setPayPerName(String payPerName) {
    this.payPerName = payPerName;
  }

  public String getOneLiner() {
    return oneLiner;
  }

  public void setOneLiner(String oneLiner) {
    this.oneLiner = oneLiner;
  }

  public String getMainBideoEmbed() {
    return mainBideoEmbed;
  }

  public void setMainBideoEmbed(String mainBideoEmbed) {
    this.mainBideoEmbed = mainBideoEmbed;
  }

  public String getMainVideoUrl() {
    return mainVideoUrl;
  }

  public void setMainVideoUrl(String mainVideoUrl) {
    this.mainVideoUrl = mainVideoUrl;
  }

  public String getImageSmallUrl() {
    return imageSmallUrl;
  }

  public void setImageSmallUrl(String imageSmallUrl) {
    this.imageSmallUrl = imageSmallUrl;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getThanksVideoUrl() {
    return thanksVideoUrl;
  }

  public void setThanksVideoUrl(String thanksVideoUrl) {
    this.thanksVideoUrl = thanksVideoUrl;
  }

  public String getThanksEmbed() {
    return thanksEmbed;
  }

  public void setThanksEmbed(String thanksEmbed) {
    this.thanksEmbed = thanksEmbed;
  }

  public String getThanksMsg() {
    return thanksMsg;
  }

  public void setThanksMsg(String thanksMsg) {
    this.thanksMsg = thanksMsg;
  }

  public boolean isMonthly() {
    return isMonthly;
  }

  public void setMonthly(boolean monthly) {
    isMonthly = monthly;
  }

  public boolean isNsfw() {
    return isNsfw;
  }

  public void setNsfw(boolean nsfw) {
    isNsfw = nsfw;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(String publishedAt) {
    this.publishedAt = publishedAt;
  }

  public String getPledgeUrl() {
    return pledgeUrl;
  }

  public void setPledgeUrl(String pledgeUrl) {
    this.pledgeUrl = pledgeUrl;
  }

  public int getPledgeSum() {
    return pledgeSum;
  }

  public void setPledgeSum(int pledgeSum) {
    this.pledgeSum = pledgeSum;
  }

  public int getPatronCount() {
    return patronCount;
  }

  public void setPatronCount(int patronCount) {
    this.patronCount = patronCount;
  }

  public int getCreationCount() {
    return creationCount;
  }

  public void setCreationCount(int creationCount) {
    this.creationCount = creationCount;
  }

  public int getOutstandingPaymentAmountCents() {
    return outstandingPaymentAmountCents;
  }

  public void setOutstandingPaymentAmountCents(int outstandingPaymentAmountCents) {
    this.outstandingPaymentAmountCents = outstandingPaymentAmountCents;
  }
}
