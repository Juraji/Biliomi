package nl.juraji.biliomi.io.api.patreon.v1.model.rewards;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 12-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonReward")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonRewardAttributes {

  @XmlElement(name = "amount_cents")
  private String amountCents;

  @XmlElement(name = "created_at")
  private String createdAt;

  @XmlElement(name = "description")
  private String description;

  @XmlElement(name = "patron_count")
  private String patronCount;

  @XmlElement(name = "title")
  private String title;

  public String getAmountCents() {
    return amountCents;
  }

  public void setAmountCents(String amountCents) {
    this.amountCents = amountCents;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPatronCount() {
    return patronCount;
  }

  public void setPatronCount(String patronCount) {
    this.patronCount = patronCount;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
