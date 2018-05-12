package nl.juraji.biliomi.io.api.twitch.helix.webhooks.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 3-1-2018.
 * Biliomi
 */
@XmlRootElement(name = "WebhookNotification")
@XmlAccessorType(XmlAccessType.FIELD)
public class NewFollowWebhookNotification extends WebhookNotification<NewFollowWebhookNotification.FollowDetails> {

    @XmlRootElement(name = "FollowDetails")
    @XmlAccessorType(XmlAccessType.FIELD)
    public class FollowDetails {

        @XmlElement(name = "from_id")
        private String fromId;

        @XmlElement(name = "to_id")
        private String toId;

        public String getFromId() {
            return fromId;
        }

        public void setFromId(String fromId) {
            this.fromId = fromId;
        }

        public String getToId() {
            return toId;
        }

        public void setToId(String toId) {
            this.toId = toId;
        }
    }
}
