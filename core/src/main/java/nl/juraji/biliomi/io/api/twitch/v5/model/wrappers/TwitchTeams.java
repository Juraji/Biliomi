package nl.juraji.biliomi.io.api.twitch.v5.model.wrappers;

import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchTeam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 8-9-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchTeams")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchTeams {

  @XmlElement(name = "teams")
  private List<TwitchTeam> teams;

  public List<TwitchTeam> getTeams() {
    return teams;
  }

  public void setTeams(List<TwitchTeam> teams) {
    this.teams = teams;
  }
}
