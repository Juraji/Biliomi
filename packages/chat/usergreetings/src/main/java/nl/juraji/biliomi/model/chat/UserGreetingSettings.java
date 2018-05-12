package nl.juraji.biliomi.model.chat;

import nl.juraji.biliomi.model.core.settings.Settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 21-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "UserGreetingSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserGreetingSettings extends Settings {

    @Column
    @XmlElement(name = "EnableGreetings")
    private boolean enableGreetings;

    @Column
    @XmlElement(name = "GreetingTimeout")
    private long greetingTimeout;

    public boolean isEnableGreetings() {
        return enableGreetings;
    }

    public void setEnableGreetings(boolean enableGreetings) {
        this.enableGreetings = enableGreetings;
    }

    public long getGreetingTimeout() {
        return greetingTimeout;
    }

    public void setGreetingTimeout(long greetingTimeout) {
        this.greetingTimeout = greetingTimeout;
    }

    @Override
    public void setDefaultValues() {
        enableGreetings = true;
        greetingTimeout = TimeUnit.MILLISECONDS.convert(6, TimeUnit.HOURS);
    }
}
