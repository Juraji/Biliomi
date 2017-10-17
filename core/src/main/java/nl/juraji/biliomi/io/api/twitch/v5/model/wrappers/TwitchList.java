package nl.juraji.biliomi.io.api.twitch.v5.model.wrappers;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchList")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class TwitchList {

  @XmlElement(name = "_total")
  private int total;

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }
}
