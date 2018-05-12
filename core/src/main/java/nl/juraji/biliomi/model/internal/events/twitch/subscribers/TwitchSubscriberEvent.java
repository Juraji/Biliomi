package nl.juraji.biliomi.model.internal.events.twitch.subscribers;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.twitch.TwitchEvent;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 3-9-2017.
 * Biliomi v3
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchSubscriberEvent extends TwitchEvent {

    @XmlElement(name = "User")
    private final User user;

    @XmlElement(name = "Timestamp")
    private DateTime timeStamp;

    @XmlElement(name = "SubPlan")
    private SubscriberPlanType subPlan;

    @XmlElement(name = "IsResub")
    private boolean isResub;

    public TwitchSubscriberEvent(User user, DateTime timeStamp, SubscriberPlanType subPlan, boolean isResub) {
        this.user = user;
        this.timeStamp = timeStamp;
        this.subPlan = subPlan;
        this.isResub = isResub;
    }

    public User getUser() {
        return user;
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public SubscriberPlanType getSubPlan() {
        return subPlan;
    }

    public boolean isResub() {
        return isResub;
    }
}
