package nl.juraji.biliomi.io.web.oauthflow.grants.code.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@XmlRootElement(name = "OAuthAccessTokenResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class OAuthAccessTokenResponse {

  @XmlElement(name = "access_token")
  private String accessToken;

  @XmlElement(name = "expires_in")
  private int expiresIn;

  @XmlElement(name = "refresh_token")
  private String refreshToken;

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(int expiresIn) {
    this.expiresIn = expiresIn;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
