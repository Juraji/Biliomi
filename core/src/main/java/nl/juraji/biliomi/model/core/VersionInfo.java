package nl.juraji.biliomi.model.core;

import org.joda.time.DateTime;

import javax.enterprise.inject.Vetoed;
import javax.xml.bind.annotation.*;

/**
 * Created by Juraji on 20-6-2017.
 * Biliomi v3
 */
@Vetoed // Do not inject, use the producer instead
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VersionInfo {

  @XmlElement(name = "Version")
  private String version;

  @XmlTransient
  private String userAgent;

  @XmlElement(name = "BuildDate")
  private DateTime buildDate;

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public DateTime getBuildDate() {
    return buildDate;
  }

  public void setBuildDate(DateTime buildDate) {
    this.buildDate = buildDate;
  }
}
