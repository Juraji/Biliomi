package nl.juraji.biliomi.utility.events;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 23-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "Event")
public class Event {

    @XmlElement(name = "EventType")
    private final String eventType = getClass().getSimpleName();

    public String getEventType() {
        return eventType;
    }
}
