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
@XmlRootElement(name = "AnnouncementsSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class AnnouncementsSettings extends Settings {

    @Column
    @XmlElement(name = "Enabled")
    private boolean enabled;

    @Column
    @XmlElement(name = "Shuffle")
    private boolean shuffle;

    @Column
    @XmlElement(name = "RunInterval")
    private long runInterval;

    @Column
    @XmlElement(name = "MinChatMessages")
    private int minChatMessages;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public long getRunInterval() {
        return runInterval;
    }

    public void setRunInterval(long interval) {
        this.runInterval = interval;
    }

    public int getMinChatMessages() {
        return minChatMessages;
    }

    public void setMinChatMessages(int minChatMessages) {
        this.minChatMessages = minChatMessages;
    }

    @Override
    public void setDefaultValues() {
        enabled = false;
        shuffle = false;
        runInterval = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
        minChatMessages = 10;
    }
}
