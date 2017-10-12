package nl.juraji.biliomi.io.api.patreon.v1.model.pledges;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 12-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonPledge")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonPledge {

  @XmlElement(name = "id")
  private String id;

  @XmlElement(name = "attributes")
  private PatreonPledgeAttributes attributes;

  @XmlElement(name = "relationships")
  private PatreonPledgeRelationships relationships;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public PatreonPledgeAttributes getAttributes() {
    return attributes;
  }

  public void setAttributes(PatreonPledgeAttributes attributes) {
    this.attributes = attributes;
  }

  public PatreonPledgeRelationships getRelationships() {
    return relationships;
  }

  public void setRelationships(PatreonPledgeRelationships relationships) {
    this.relationships = relationships;
  }
}
