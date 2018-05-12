package nl.juraji.biliomi.model.core.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by robin.
 * april 2017
 */
@Entity
@XmlRootElement(name = "SystemSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class SystemSettings extends Settings {

    @Column
    @XmlElement(name = "Muted")
    private boolean muted;

    @Column
    @XmlElement(name = "EnableWhispers")
    private boolean enableWhispers;

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isEnableWhispers() {
        return enableWhispers;
    }

    public void setEnableWhispers(boolean enableWhispers) {
        this.enableWhispers = enableWhispers;
    }

    @Override
    public void setDefaultValues() {
        muted = true;
        enableWhispers = false;
    }
}
