package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.components;

import java.util.List;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USKillGame {
  private List<String> murders;
  private List<String> suicides;

  public List<String> getMurders() {
    return murders;
  }

  public void setMurders(List<String> murders) {
    this.murders = murders;
  }

  public List<String> getSuicides() {
    return suicides;
  }

  public void setSuicides(List<String> suicides) {
    this.suicides = suicides;
  }
}
