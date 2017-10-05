package nl.juraji.biliomi.io.api.steam.v1.model.library;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 25-5-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "SteamLibrary")
@XmlAccessorType(XmlAccessType.FIELD)
public class SteamLibrary {

  @XmlElement(name = "game_count")
  private long gameCount;

  @XmlElement(name = "games")
  private List<SteamApp> games;

  public long getGameCount() {
    return gameCount;
  }

  public void setGameCount(long gameCount) {
    this.gameCount = gameCount;
  }

  public List<SteamApp> getGames() {
    return games;
  }

  public void setGames(List<SteamApp> games) {
    this.games = games;
  }
}
