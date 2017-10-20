package nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.oauth.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "StreamLabsToken")
@XmlAccessorType(XmlAccessType.FIELD)
public class StreamLabsToken {

  @XmlElement(name = "access_token")
  private String accessToken;

  @XmlElement(name = "token_type")
  private String tokenType;

  @XmlElement(name = "refresh_token")
  private String refreshToken;

  @XmlElement(name = "expires_in")
  private int expiresIn;

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public int getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(int expiresIn) {
    this.expiresIn = expiresIn;
  }
}
