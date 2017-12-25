package nl.juraji.biliomi.model.internal.rest.auth;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 24-12-2017.
 * Biliomi
 */
@XmlRootElement(name = "RestRefreshTokenRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestRefreshTokenRequest {

  @XmlElement(name = "RefreshToken")
  private String refreshToken;

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
