package nl.juraji.biliomi.model.core.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 6-9-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriberWatchSettings extends Settings {

  @Column
  @XmlElement(name = "RewardTier1")
  private long rewardTier1;

  @Column
  @XmlElement(name = "RewardTier2")
  private long rewardTier2;

  @Column
  @XmlElement(name = "RewardTier3")
  private long rewardTier3;

  public long getRewardTier1() {
    return rewardTier1;
  }

  public void setRewardTier1(long rewardTier1) {
    this.rewardTier1 = rewardTier1;
  }

  public long getRewardTier2() {
    return rewardTier2;
  }

  public void setRewardTier2(long rewardTier2) {
    this.rewardTier2 = rewardTier2;
  }

  public long getRewardTier3() {
    return rewardTier3;
  }

  public void setRewardTier3(long rewardTier3) {
    this.rewardTier3 = rewardTier3;
  }

  @Override
  public void setDefaultValues() {
    rewardTier1 = 1000;
    rewardTier2 = 2000;
    rewardTier3 = 3000;
  }
}
