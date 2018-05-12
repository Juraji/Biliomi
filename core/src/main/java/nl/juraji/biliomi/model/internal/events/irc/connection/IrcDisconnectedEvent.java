package nl.juraji.biliomi.model.internal.events.irc.connection;

import nl.juraji.biliomi.model.internal.events.irc.IrcEvent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 25-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "IrcDisconnectedEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class IrcDisconnectedEvent extends IrcEvent {

    @XmlElement(name = "Status")
    private final int status;

    @XmlElement(name = "Reason")
    private final String reason;

    @XmlElement(name = "Remote")
    private final boolean remote;

    @XmlElement(name = "WillRestart")
    private final boolean willRestart;

    public IrcDisconnectedEvent(int status, String reason, boolean remote, boolean willRestart) {
        this.status = status;
        this.reason = reason;
        this.remote = remote;
        this.willRestart = willRestart;
    }

    public int getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public boolean isRemote() {
        return remote;
    }

    public boolean isWillRestart() {
        return willRestart;
    }
}
