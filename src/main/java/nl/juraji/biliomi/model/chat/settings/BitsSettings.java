package nl.juraji.biliomi.model.chat.settings;

import nl.juraji.biliomi.model.core.settings.Settings;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 10-9-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BitsSettings extends Settings {

  @XmlElement(name = "EnableBitsToPoints")
  private boolean enableBitsToPoints;

  @XmlElement(name = "BitsToPointsPayoutToAllChatters")
  private boolean bitsToPointsPayoutToAllChatters;

  @XmlElement(name = "BitsToPointsMultiplier")
  private double bitsToPointsMultiplier;

  public boolean isEnableBitsToPoints() {
    return enableBitsToPoints;
  }

  public void setEnableBitsToPoints(boolean enableBitsToPoints) {
    this.enableBitsToPoints = enableBitsToPoints;
  }

  public boolean isBitsToPointsPayoutToAllChatters() {
    return bitsToPointsPayoutToAllChatters;
  }

  public void setBitsToPointsPayoutToAllChatters(boolean bitsToPointsPayoutToAllChatters) {
    this.bitsToPointsPayoutToAllChatters = bitsToPointsPayoutToAllChatters;
  }

  public double getBitsToPointsMultiplier() {
    return bitsToPointsMultiplier;
  }

  public void setBitsToPointsMultiplier(double bitsToPointsMultiplier) {
    this.bitsToPointsMultiplier = bitsToPointsMultiplier;
  }

  @Override
  public void setDefaultValues() {
    enableBitsToPoints = true;
    bitsToPointsPayoutToAllChatters = true;
    bitsToPointsMultiplier = 1;
  }
}
