package nl.juraji.biliomi.io.api.patreon.v1.model;

import nl.juraji.biliomi.io.api.patreon.v1.model.campaigns.PatreonCampaign;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 12-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonCampaigns")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonCampaigns extends PatreonDataWrapper<List<PatreonCampaign>> {
}
