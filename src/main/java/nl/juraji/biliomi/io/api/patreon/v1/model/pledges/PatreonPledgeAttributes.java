package nl.juraji.biliomi.io.api.patreon.v1.model.pledges;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 12-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonPledgeAttributes")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonPledgeAttributes {

  @XmlElement(name = "amount_cents")
  private int amountCents;

  @XmlElement(name = "created_at")
  private String createdAt;

  @XmlElement(name = "pledge_cap_cents")
  private int pledgeCapCents;

  @XmlElement(name = "patron_pays_fees")
  private boolean patronPaysFees;

  public int getAmountCents() {
    return amountCents;
  }

  public void setAmountCents(int amountCents) {
    this.amountCents = amountCents;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public int getPledgeCapCents() {
    return pledgeCapCents;
  }

  public void setPledgeCapCents(int pledgeCapCents) {
    this.pledgeCapCents = pledgeCapCents;
  }

  public boolean isPatronPaysFees() {
    return patronPaysFees;
  }

  public void setPatronPaysFees(boolean patronPaysFees) {
    this.patronPaysFees = patronPaysFees;
  }
}
