package nl.juraji.biliomi.io.api.patreon.v1.model.campaigns;

import nl.juraji.biliomi.io.api.patreon.v1.model.campaigns.goals.PatreonGoal;
import nl.juraji.biliomi.io.api.patreon.v1.model.pledges.PatreonPledge;
import nl.juraji.biliomi.io.api.patreon.v1.model.pledges.rewards.PatreonReward;
import nl.juraji.biliomi.io.api.patreon.v1.model.users.PatreonUser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 11-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonCampaignRelationships")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonCampaignRelationships {

  @XmlElement(name = "creator")
  private PatreonUser creator;

  @XmlElement(name = "rewards")
  private List<PatreonReward> rewards;

  @XmlElement(name = "goals")
  private List<PatreonGoal> goals;

  @XmlElement(name = "pledges")
  private List<PatreonPledge> pledges;

  public PatreonUser getCreator() {
    return creator;
  }

  public void setCreator(PatreonUser creator) {
    this.creator = creator;
  }

  public List<PatreonReward> getRewards() {
    return rewards;
  }

  public void setRewards(List<PatreonReward> rewards) {
    this.rewards = rewards;
  }

  public List<PatreonGoal> getGoals() {
    return goals;
  }

  public void setGoals(List<PatreonGoal> goals) {
    this.goals = goals;
  }

  public List<PatreonPledge> getPledges() {
    return pledges;
  }

  public void setPledges(List<PatreonPledge> pledges) {
    this.pledges = pledges;
  }
}
