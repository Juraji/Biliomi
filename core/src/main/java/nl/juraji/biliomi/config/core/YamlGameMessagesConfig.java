package nl.juraji.biliomi.config.core;

import java.util.List;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
public class YamlGameMessagesConfig {
  private List<String> wins;
  private List<String> losts;

  public List<String> getWins() {
    return wins;
  }

  public void setWins(List<String> wins) {
    this.wins = wins;
  }

  public List<String> getLosts() {
    return losts;
  }

  public void setLosts(List<String> losts) {
    this.losts = losts;
  }
}
