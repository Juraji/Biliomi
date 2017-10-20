package nl.juraji.biliomi.components.integrations.steam.api.v1.model.players;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 5-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "SteamPlayer")
@XmlAccessorType(XmlAccessType.FIELD)
public class SteamPlayer {

  @XmlElement(name = "steamid")
  private long steamId;

  @XmlElement(name = "communityvisibilitystate")
  private SteamProfileVisibilityState profileVisibilityState;

  @XmlElement(name = "personaname")
  private String personaName;

  @XmlElement(name = "gameid")
  private Long currentGameId;

  public long getSteamId() {
    return steamId;
  }

  public void setSteamId(long steamId) {
    this.steamId = steamId;
  }

  public SteamProfileVisibilityState getProfileVisibilityState() {
    return profileVisibilityState;
  }

  public void setProfileVisibilityState(SteamProfileVisibilityState profileVisibilityState) {
    this.profileVisibilityState = profileVisibilityState;
  }

  public String getPersonaName() {
    return personaName;
  }

  public void setPersonaName(String personaName) {
    this.personaName = personaName;
  }

  public Long getCurrentGameId() {
    return currentGameId;
  }

  public void setCurrentGameId(Long currentGameId) {
    this.currentGameId = currentGameId;
  }
}
