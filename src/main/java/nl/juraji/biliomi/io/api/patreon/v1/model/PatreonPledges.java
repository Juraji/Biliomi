package nl.juraji.biliomi.io.api.patreon.v1.model;

import nl.juraji.biliomi.io.api.patreon.v1.model.PatreonDataWrapper;
import nl.juraji.biliomi.io.api.patreon.v1.model.metadata.PatreonLinks;
import nl.juraji.biliomi.io.api.patreon.v1.model.metadata.PatreonMeta;
import nl.juraji.biliomi.io.api.patreon.v1.model.pledges.PatreonPledge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 12-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonPledges")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonPledges extends PatreonDataWrapper<List<PatreonPledge>> {

  @XmlElement(name = "links")
  private PatreonLinks links;

  @XmlElement(name = "meta")
  private PatreonMeta meta;

  public PatreonLinks getLinks() {
    return links;
  }

  public void setLinks(PatreonLinks links) {
    this.links = links;
  }
}
