package nl.juraji.biliomi.model.chat;

import nl.juraji.biliomi.model.core.settings.Settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 18-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "HostWatchSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class HostWatchSettings extends Settings {

  @Column
  @XmlElement(name = "Reward")
  private long reward = 0;

  public long getReward() {
    return reward;
  }

  public void setReward(long reward) {
    this.reward = reward;
  }

  @Override
  public void setDefaultValues() {
    this.reward = 0;
  }
}
