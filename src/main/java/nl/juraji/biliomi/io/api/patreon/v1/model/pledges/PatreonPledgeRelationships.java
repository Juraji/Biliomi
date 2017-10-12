package nl.juraji.biliomi.io.api.patreon.v1.model.pledges;

import nl.juraji.biliomi.io.api.patreon.v1.model.rewards.PatreonReward;
import nl.juraji.biliomi.io.api.patreon.v1.model.users.PatreonUser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 12-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonPledgeRelationships")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonPledgeRelationships {

  @XmlElement(name = "patron")
  private PatreonUser patron;

  @XmlElement(name = "reward")
  private PatreonReward reward;

  public PatreonUser getPatron() {
    return patron;
  }

  public void setPatron(PatreonUser patron) {
    this.patron = patron;
  }

  public PatreonReward getReward() {
    return reward;
  }

  public void setReward(PatreonReward reward) {
    this.reward = reward;
  }
}
