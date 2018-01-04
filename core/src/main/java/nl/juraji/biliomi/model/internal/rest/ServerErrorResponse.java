package nl.juraji.biliomi.model.internal.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 24-12-2017.
 * Biliomi
 */
@XmlRootElement(name = "ServerErrorResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerErrorResponse {

  @XmlElement(name = "ErrorMessage")
  private String errorMessage;

  @XmlElement(name = "CausedBy")
  private String causedBy;

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getCausedBy() {
    return causedBy;
  }

  public void setCausedBy(String causedBy) {
    this.causedBy = causedBy;
  }
}
