package nl.juraji.biliomi.model.games;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 24-5-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "UserRecordStats")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserRecordStats {

  @XmlElement(name = "RecordCount")
  private final long recordCount;

  @XmlElement(name = "Losses")
  protected final long losses;

  @XmlElement(name = "Wins")
  protected final long wins;

  @XmlElement(name = "IsMoreWins")
  private final boolean isMoreWins;

  public UserRecordStats(long recordCount, long losses, long wins) {
    this.recordCount = recordCount;
    this.losses = losses;
    this.wins = wins;
    this.isMoreWins = wins > losses;
  }

  public long getRecordCount() {
    return recordCount;
  }

  public long getLosses() {
    return losses;
  }

  public long getWins() {
    return wins;
  }

  public boolean isMoreWins() {
    return isMoreWins;
  }
}
