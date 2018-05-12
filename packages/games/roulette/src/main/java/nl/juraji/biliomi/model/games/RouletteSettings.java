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
 * Created by Juraji on 24-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "RouletteSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class RouletteSettings extends Settings {

    @Column
    @XmlElement(name = "TimeoutOnDeathEnabled")
    private boolean timeoutOnDeathEnabled;

    @Column
    @XmlElement(name = "TimeoutOnDeath")
    private long timeoutOnDeath;

    public boolean isTimeoutOnDeathEnabled() {
        return timeoutOnDeathEnabled;
    }

    public void setTimeoutOnDeathEnabled(boolean timeoutOnDeathEnabled) {
        this.timeoutOnDeathEnabled = timeoutOnDeathEnabled;
    }

    public long getTimeoutOnDeath() {
        return timeoutOnDeath;
    }

    public void setTimeoutOnDeath(long timeoutOnDeath) {
        this.timeoutOnDeath = timeoutOnDeath;
    }

    @Override
    public void setDefaultValues() {
        timeoutOnDeathEnabled = true;
        timeoutOnDeath = TimeUnit.MILLISECONDS.convert(3, TimeUnit.MINUTES);
    }
}
