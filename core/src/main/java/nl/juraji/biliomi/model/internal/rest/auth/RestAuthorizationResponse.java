package nl.juraji.biliomi.model.internal.rest.auth;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 16-6-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "RestAuthorizationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestAuthorizationResponse {

  @XmlElement(name = "AuthorizationToken")
  private String authorizationToken;

  @XmlElement(name = "RefreshToken")
  private String refreshToken;

  @XmlElement(name = "Message")
  private String message;

  public String getAuthorizationToken() {
    return authorizationToken;
  }

  public void setAuthorizationToken(String authorizationToken) {
    this.authorizationToken = authorizationToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public static String getFaultAuthorizationMissingMsg() {
    return "Authorization header is empty or not present";
  }

  public static String getFaultInvalidTokenMsg() {
    return "Token unuseable";
  }

  public static String getFaultLoginEmptyMsg() {
    return "Username or password is empty";
  }

  public static String getFaultUsernameInvalidMsg() {
    return "Invalid username";
  }

  public static String getFaultPasswordInvalidMsg() {
    return "Invalid password";
  }

  public static String getLoginOkMsg() {
    return "Login OK";
  }
}
