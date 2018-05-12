package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.utility.calculate.MathUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 5-6-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "UserRecordStats")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAdventureRecordStats extends UserRecordStats {

    @XmlElement(name = "TotalPayout")
    private final long totalPayout;

    @XmlElement(name = "PercentageByTamagotchi")
    private final double percentageByTamagotchi;

    public UserAdventureRecordStats(long recordCount, long losses, long wins, long totalPayout, long byTamagotchiCount) {
        super(recordCount, losses, wins);
        this.totalPayout = totalPayout;
        this.percentageByTamagotchi = MathUtils.calcPercentage(byTamagotchiCount, getWins());
    }

    public long getTotalPayout() {
        return totalPayout;
    }

    public double getPercentageByTamagotchi() {
        return percentageByTamagotchi;
    }
}
