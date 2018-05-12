package nl.juraji.biliomi.model.internal.twitch.hosting;

import nl.juraji.biliomi.model.internal.events.twitch.TwitchEvent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 23-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchHostInEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchHostInEvent extends TwitchEvent {

    @XmlElement(name = "ChannelName")
    private final String channelName;

    @XmlElement(name = "ChannelId")
    private final Integer channelId;

    @XmlElement(name = "IsAuto")
    private final boolean isAuto;

    public TwitchHostInEvent(String channelName, Integer channelId, boolean isAuto) {
        this.channelName = channelName;
        this.channelId = channelId;
        this.isAuto = isAuto;
    }

    public String getChannelName() {
        return channelName;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public boolean isAuto() {
        return isAuto;
    }
}
