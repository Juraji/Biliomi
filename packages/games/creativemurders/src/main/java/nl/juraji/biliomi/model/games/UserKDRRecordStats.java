package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.calculate.MathUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Juraji on 22-5-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "UserRecordStats")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserKDRRecordStats extends UserRecordStats {

    @XmlElement(name = "Suicides")
    private final long suicides;

    @XmlElement(name = "FavoriteTarget")
    private final User favoriteTarget;

    @XmlElement(name = "KDR")
    private final String kdr;

    public UserKDRRecordStats(long recordCount, long kills, long deaths, long suicides, User favoriteTarget) {
        super(recordCount, deaths, kills);
        this.suicides = suicides;
        this.favoriteTarget = favoriteTarget;

        // The KDR ratio as KILLS/DEATHS/SUICIDES
        String suicideRatio;
        if (suicides > 0) {
            suicideRatio = new BigDecimal((double) losses / (double) suicides)
                    .setScale(1, RoundingMode.HALF_UP)
                    .toString();
        } else {
            suicideRatio = "0";
        }

        this.kdr = MathUtils.asFraction(wins, losses) + '/' + suicideRatio;
    }

    public long getSuicides() {
        return suicides;
    }

    public User getFavoriteTarget() {
        return favoriteTarget;
    }

    public String getKdr() {
        return kdr;
    }
}
