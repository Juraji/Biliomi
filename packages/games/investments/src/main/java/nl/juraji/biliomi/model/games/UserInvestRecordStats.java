package nl.juraji.biliomi.model.games;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 27-5-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "UserRecordStats")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserInvestRecordStats extends UserRecordStats {

    @XmlElement(name = "TotalInvested")
    private final long totalInvested;

    @XmlElement(name = "TotalEarned")
    private final long totalEarned;

    public UserInvestRecordStats(long recordCount, long losses, long wins, long totalInvested, long totalEarned) {
        super(recordCount, losses, wins);
        this.totalInvested = totalInvested;
        this.totalEarned = totalEarned;
    }

    public long getTotalInvested() {
        return totalInvested;
    }

    public long getTotalEarned() {
        return totalEarned;
    }
}
