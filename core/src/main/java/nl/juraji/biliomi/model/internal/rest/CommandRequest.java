package nl.juraji.biliomi.model.internal.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "CommandRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommandRequest {

  @XmlElement(name = "Command")
  private String command;

  public void setCommand(String command) {
    this.command = command;
  }

  public String getCommand() {
    return command;
  }
}
