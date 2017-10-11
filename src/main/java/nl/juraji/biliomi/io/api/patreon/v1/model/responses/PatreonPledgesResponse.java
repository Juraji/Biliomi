package nl.juraji.biliomi.io.api.patreon.v1.model.responses;

import nl.juraji.biliomi.io.api.patreon.v1.model.PatreonDataWrapper;
import nl.juraji.biliomi.io.api.patreon.v1.model.pledges.PatreonPledge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 11-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonPledgesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonPledgesResponse extends PatreonDataWrapper<List<PatreonPledge>> {
}
