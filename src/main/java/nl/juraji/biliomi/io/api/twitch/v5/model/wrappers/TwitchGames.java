package nl.juraji.biliomi.io.api.twitch.v5.model.wrappers;

import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchGame;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchGames")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchGames {

  @XmlElement(name = "games")
  private List<TwitchGame> games;

  public List<TwitchGame> getGames() {
    return games;
  }

  public void setGames(List<TwitchGame> games) {
    this.games = games;
  }
}
