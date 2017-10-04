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
  public static final String MSG_FAULT_AUTHORIZATION_MISSING = "Authorization header is empty or not present";
  public static final String MSG_FAULT_LOGIN_EMPTY = "Username or password is empty";
  public static final String MSG_FAULT_USERNAME_INVALID = "Invalid username";
  public static final String MSG_FAULT_PASSWORD_INVALID = "Invalid password";
  public static final String MSG_LOGIN_OK = "Login OK";

  @XmlElement(name = "Token")
  private String token;

  @XmlElement(name = "Message")
  private String message;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
