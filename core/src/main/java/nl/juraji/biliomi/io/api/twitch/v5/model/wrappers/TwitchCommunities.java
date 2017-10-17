package nl.juraji.biliomi.io.api.twitch.v5.model.wrappers;

import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchCommunity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 8-9-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchCommunities")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchCommunities {

  @XmlElement(name = "communities")
  private List<TwitchCommunity> communities;

  public List<TwitchCommunity> getCommunities() {
    return communities;
  }

  public void setCommunities(List<TwitchCommunity> communities) {
    this.communities = communities;
  }
}
