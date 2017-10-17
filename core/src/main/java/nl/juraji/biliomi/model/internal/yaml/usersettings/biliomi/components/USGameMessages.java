package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.components;

import java.util.List;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USGameMessages {
  private List<String> win;
  private List<String> lost;

  public List<String> getWin() {
    return win;
  }

  public void setWin(List<String> win) {
    this.win = win;
  }

  public List<String> getLost() {
    return lost;
  }

  public void setLost(List<String> lost) {
    this.lost = lost;
  }
}
