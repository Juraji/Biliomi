package nl.juraji.biliomi.io.api.twitch.v5.model.wrappers;

import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchSubscription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchSubscriptions")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchSubscriptions extends TwitchList {

    @XmlElement(name = "subscriptions")
    private List<TwitchSubscription> subscriptions;

    public List<TwitchSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<TwitchSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
