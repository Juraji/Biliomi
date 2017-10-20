package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.settings.Settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 27-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "InvestmentSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvestmentSettings extends Settings {

  @Column
  @XmlElement(name = "InvestmentDuration")
  private long investmentDuration;

  @Column
  @XmlElement(name = "MinInterest")
  private double minInterest;

  @Column
  @XmlElement(name = "MaxInterest")
  private double maxInterest;

  public long getInvestmentDuration() {
    return investmentDuration;
  }

  public void setInvestmentDuration(long investmentDuration) {
    this.investmentDuration = investmentDuration;
  }

  public double getMinInterest() {
    return minInterest;
  }

  public void setMinInterest(double minInvestment) {
    this.minInterest = minInvestment;
  }

  public double getMaxInterest() {
    return maxInterest;
  }

  public void setMaxInterest(double maxInvestment) {
    this.maxInterest = maxInvestment;
  }

  @Override
  public void setDefaultValues() {
    investmentDuration = TimeUnit.MILLISECONDS.convert(15, TimeUnit.MINUTES);
    minInterest = 0.05; // 5%
    maxInterest = 0.25; // 25%
  }
}
