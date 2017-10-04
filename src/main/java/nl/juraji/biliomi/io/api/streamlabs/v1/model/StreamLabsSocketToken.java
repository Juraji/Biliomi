package nl.juraji.biliomi.io.api.streamlabs.v1.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "StreamLabsSocketToken")
@XmlAccessorType(XmlAccessType.FIELD)
public class StreamLabsSocketToken {

  @XmlElement(name = "socket_token")
  private String token;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
