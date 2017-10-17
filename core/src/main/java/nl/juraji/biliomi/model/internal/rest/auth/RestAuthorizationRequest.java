package nl.juraji.biliomi.model.internal.rest.auth;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 16-6-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "RestAuthorizationRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestAuthorizationRequest {

  @XmlElement(name = "Username")
  private String username;

  @XmlElement(name = "Password")
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isEmpty() {
    return StringUtils.isEmpty(username) || StringUtils.isEmpty(password);
  }
}
