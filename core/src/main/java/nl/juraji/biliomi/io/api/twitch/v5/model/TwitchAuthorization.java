package nl.juraji.biliomi.io.api.twitch.v5.model;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by Juraji on 21-4-2017.
 * Biliomi v3
 */
public class TwitchAuthorization {

  @XmlElement(name = "created_at")
  private String createdAt;

  @XmlElement(name = "scopes")
  private List<String> scopes;

  @XmlElement(name = "updated_at")
  private String updatedAt;

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public List<String> getScopes() {
    return scopes;
  }

  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }
}
