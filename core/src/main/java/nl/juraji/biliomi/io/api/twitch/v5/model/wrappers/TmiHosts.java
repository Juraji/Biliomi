package nl.juraji.biliomi.io.api.twitch.v5.model.wrappers;

import nl.juraji.biliomi.io.api.twitch.v5.model.TmiHost;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TmiHosts")
@XmlAccessorType(XmlAccessType.FIELD)
public class TmiHosts {

  @XmlElement(name = "hosts")
  private List<TmiHost> hosts;

  public List<TmiHost> getHosts() {
    return hosts;
  }

  public void setHosts(List<TmiHost> hosts) {
    this.hosts = hosts;
  }
}
