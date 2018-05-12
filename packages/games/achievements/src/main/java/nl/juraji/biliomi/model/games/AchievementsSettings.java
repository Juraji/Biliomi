package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.settings.Settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AchievementsSettings extends Settings {

    @Column
    @XmlElement(name = "AchievementsEnabled")
    private boolean achievementsEnabled;

    public boolean isAchievementsEnabled() {
        return achievementsEnabled;
    }

    public void setAchievementsEnabled(boolean achievementsEnabled) {
        this.achievementsEnabled = achievementsEnabled;
    }

    @Override
    public void setDefaultValues() {
        achievementsEnabled = true;
    }
}
