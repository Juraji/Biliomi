package nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.v1.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 3-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "StreamLabsTwitchUser")
@XmlAccessorType(XmlAccessType.FIELD)
public class StreamLabsTwitchUser {

    @XmlElement(name = "twitch")
    private StreamLabsUser user;

    public StreamLabsUser getUser() {
        return user;
    }

    public void setUser(StreamLabsUser user) {
        this.user = user;
    }
}
