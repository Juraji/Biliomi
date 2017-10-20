package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.settings.Settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 5-6-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "AdventureSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class AdventureSettings extends Settings {

  @Column
  @XmlElement(name = "JoinTimeout")
  private long joinTimeout;

  @Column
  @XmlElement(name = "MinimumBet")
  private long minimumBet;

  @Column
  @XmlElement(name = "MaximumBet")
  private long maximumBet;

  @Column
  @XmlElement(name = "Cooldown")
  private long cooldown;

  @Column
  @XmlElement(name = "WinMultiplier")
  private double winMultiplier;

  public long getJoinTimeout() {
    return joinTimeout;
  }

  public void setJoinTimeout(long joinTimeout) {
    this.joinTimeout = joinTimeout;
  }

  public long getMinimumBet() {
    return minimumBet;
  }

  public void setMinimumBet(long minimumBet) {
    this.minimumBet = minimumBet;
  }

  public long getMaximumBet() {
    return maximumBet;
  }

  public void setMaximumBet(long maximumBet) {
    this.maximumBet = maximumBet;
  }

  public long getCooldown() {
    return cooldown;
  }

  public void setCooldown(long cooldown) {
    this.cooldown = cooldown;
  }

  public double getWinMultiplier() {
    return winMultiplier;
  }

  public void setWinMultiplier(double winMultiplier) {
    this.winMultiplier = winMultiplier;
  }

  @Override
  public void setDefaultValues() {
    joinTimeout = 180000;
    minimumBet = 10;
    maximumBet = 1000;
    cooldown = 900000;
    winMultiplier = 0.3;
  }
}
