package nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.socket.model;

import com.fasterxml.jackson.databind.JsonNode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "SocketEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class SocketEvent {

    @XmlElement(name = "type")
    private SocketEventType type;

    @XmlElement(name = "for")
    private SocketEventForType forAccountType;

    @XmlElement(name = "message")
    private Set<JsonNode> message;

    public SocketEventType getType() {
        return type;
    }

    public void setType(SocketEventType type) {
        this.type = type;
    }

    public SocketEventForType getForAccountType() {
        return forAccountType;
    }

    public void setForAccountType(SocketEventForType forAccountType) {
        this.forAccountType = forAccountType;
    }

    public Set<JsonNode> getMessage() {
        return message;
    }

    public void setMessage(Set<JsonNode> message) {
        this.message = message;
    }
}
