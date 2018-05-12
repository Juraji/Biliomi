package nl.juraji.biliomi.model.core.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 27-4-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "FollowerWatchSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class FollowerWatchSettings extends Settings {

    @Column
    @XmlElement(name = "Reward")
    private long reward;

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
