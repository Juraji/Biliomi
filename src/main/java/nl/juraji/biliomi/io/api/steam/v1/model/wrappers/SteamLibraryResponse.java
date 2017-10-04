package nl.juraji.biliomi.io.api.steam.v1.model.wrappers;

import nl.juraji.biliomi.io.api.steam.v1.model.SteamLibrary;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 25-5-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "SteamLibraryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class SteamLibraryResponse {

  @XmlElement(name = "response")
  private SteamLibrary response;

  public SteamLibrary getResponse() {
    return response;
  }

  public void setResponse(SteamLibrary response) {
    this.response = response;
  }
}
