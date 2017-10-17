package nl.juraji.biliomi.io.api.twitch.v5.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 21-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchToken")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchToken {

  @XmlElement(name = "authorization")
  private TwitchAuthorization authorization;

  @XmlElement(name = "client_id")
  private String clientId;

  @XmlElement(name = "user_id")
  private String userId;

  @XmlElement(name = "user_name")
  private String username;

  @XmlElement(name = "valid")
  private Boolean valid;

  public TwitchAuthorization getAuthorization() {
    return authorization;
  }

  public void setAuthorization(TwitchAuthorization authorization) {
    this.authorization = authorization;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Boolean getValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }
}
