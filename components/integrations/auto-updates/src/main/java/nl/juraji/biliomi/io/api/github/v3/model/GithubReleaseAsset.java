package nl.juraji.biliomi.io.api.github.v3.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 6-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "GithubReleaseAsset")
@XmlAccessorType(XmlAccessType.FIELD)
public class GithubReleaseAsset {

  @XmlElement(name = "name")
  private String name;

  @XmlElement(name = "content_type")
  private String contentType;

  @XmlElement(name = "browser_download_url")
  private String downloadUrl;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }
}
