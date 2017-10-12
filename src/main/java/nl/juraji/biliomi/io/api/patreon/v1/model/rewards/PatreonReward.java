package nl.juraji.biliomi.io.api.patreon.v1.model.rewards;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 12-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonReward")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonReward {

  @XmlElement(name = "id")
  private String id;

  @XmlElement(name = "attributes")
  private PatreonRewardAttributes attributes;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public PatreonRewardAttributes getAttributes() {
    return attributes;
  }

  public void setAttributes(PatreonRewardAttributes attributes) {
    this.attributes = attributes;
  }
}
