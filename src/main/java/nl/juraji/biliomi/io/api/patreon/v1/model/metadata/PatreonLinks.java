package nl.juraji.biliomi.io.api.patreon.v1.model.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 12-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonLinks")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonLinks {

  @XmlElement(name = "first")
  private String firstPageLink;

  @XmlElement(name = "next")
  private String nextPageLink;

  public String getFirstPageLink() {
    return firstPageLink;
  }

  public void setFirstPageLink(String firstPageLink) {
    this.firstPageLink = firstPageLink;
  }

  public String getNextPageLink() {
    return nextPageLink;
  }

  public void setNextPageLink(String nextPageLink) {
    this.nextPageLink = nextPageLink;
  }
}
