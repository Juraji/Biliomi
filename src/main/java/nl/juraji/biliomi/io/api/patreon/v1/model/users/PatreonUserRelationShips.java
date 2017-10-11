package nl.juraji.biliomi.io.api.patreon.v1.model.users;

import nl.juraji.biliomi.io.api.patreon.v1.model.campaigns.PatreonCampaign;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 11-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonUserRelationShips")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonUserRelationShips {

  @XmlElement(name = "campaign")
  private PatreonCampaign campaign;

  public PatreonCampaign getCampaign() {
    return campaign;
  }

  public void setCampaign(PatreonCampaign campaign) {
    this.campaign = campaign;
  }
}
