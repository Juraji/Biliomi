package nl.juraji.biliomi.io.api.twitch.v5.model;

import nl.juraji.biliomi.utility.factories.ModelUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * Created by Juraji on 15-12-2017.
 * Biliomi
 */
@XmlRootElement(name = "TwitchCommunitiesUpdate")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchCommunitiesUpdate {

    @XmlElement(name = "community_ids")
    private Set<String> communityIds;

    public Set<String> getCommunityIds() {
        communityIds = ModelUtils.initCollection(communityIds);
        return communityIds;
    }
}
