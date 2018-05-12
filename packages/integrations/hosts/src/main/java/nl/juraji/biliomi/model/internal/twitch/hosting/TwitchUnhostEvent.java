package nl.juraji.biliomi.model.internal.twitch.hosting;

import nl.juraji.biliomi.utility.events.Event;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
@XmlRootElement(name = "TwitchUnhostEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchUnhostEvent extends Event {

    @XmlElement(name = "ChannelName")
    private final String channelName;

    public TwitchUnhostEvent(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }
}
